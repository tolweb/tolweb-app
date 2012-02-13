package beanform;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.log4j.Logger;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.binding.BindingFactory;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.validator.ValidatorsBinding;

/**
 * <p>A form that provides edit capabilities for a Java Bean. Only properties
 * that are strings, booleans, numbers or dates are considered editable. Fields
 * for read-only bean properties are automatically disabled. Field labels are
 * messages keyed on the property name. The save button (if displayed) is
 * labeled with the message corresponding to the key <tt>save</tt>. The cancel
 * button (if displayed) is labeled with the message corresponding to the key
 * <tt>cancel</tt>. The refresh button (if displayed) is labeled with the
 * message corresponding to the key <tt>refresh</tt>. The delete button (if
 * displayed) is labeled with the message corresponding to the key
 * <tt>delete</tt>.</p>
 *
 * <p>The component parameters are as follows:</p>
 *
 * <ul>
 *   <li><tt>bean</tt>: Required, specifies the Java Bean to modify.</li>
 *   <li><tt>properties</tt>: Not required, specifies the bean properties to be
 *      edited; if omitted, all eligible bean properties (strings, booleans,
 *      numbers and dates) are considered editable; if specified, properties
 *      are displayed in the specified order.</li>
 *   <li><tt>save</tt>: Not required, specifies the listener to invoke on
 *      save.</li>
 *   <li><tt>delete</tt>: Not required, specifies the listener to invoke on
 *      delete.</li>
 *   <li><tt>success</tt>: Not required, specifies the listener to invoke on
 *      form submission.</li>
 *   <li><tt>cancel</tt>: Not required, specifies the listener to invoke on
 *      form cancellation.</li>
 *   <li><tt>refresh</tt>: Not required, specifies the listener to invoke on
 *      form refresh.</li>
 *   <li><tt>delegate</tt>: Not required, specifies the validation delegate to
 *      use on the form.</li>
 *   <li>And all the rest of the <tt>Form</tt> parameters, including
 *      <tt>method</tt>, <tt>listener</tt>, <tt>stateful</tt>, <tt>direct</tt>,
 *      <tt>clientValidationEnabled</tt>, <tt>focus</tt>, <tt>scheme</tt> and
 *      <tt>port</tt>. See the <tt>Form</tt> component's documentation for
 *      more details.
 * </ul>
 *
 * <p>The generated HTML is a form containing a two-column table, unless this
 * component is inside an external Form component, in which case no extraneous
 * form is generated. The table's left column contains the field labels and the
 * right column contains the data entry fields. The bottom row spans both
 * columns and contains the save button (if the <tt>save</tt> parameter was
 * specified), the cancel button (if the <tt>cancel</tt> parameter was
 * specified), the refresh button (if the <tt>refresh</tt> parameter was
 * specified) and the delete button (if the <tt>delete</tt> parameter was
 * specified).</p>
 *
 * <p>The table can be styled using CSS: the table's CSS class is
 * <tt>beanFormTable</tt>, the left column's CSS class is
 * <tt>beanFormLeftColumn</tt>, the right column's CSS class is
 * <tt>beanFormRightColumn</tt>, and the bottom column that contains the
 * buttons has the CSS class <tt>beanFormButtonColumn</tt>.</p>
 *
 * <p>If you need to customize any of the input fields, you can do so by adding
 * a <tt>Block</tt> component to your page (with the id
 * <tt>[propertyName]BeanFieldBlock</tt>) that contains any
 * <tt>IFormComponent</tt> (with the id <tt>[propertyName]BeanField</tt>).
 * Doing this will allow you to edit properties that are considered
 * non-editable by default (ie, not a string/boolean/number/date).</p>
 *
 * <p>In order to use validation, specify the <tt>delegate</tt> parameter
 * (unless you have already specified it in an external <tt>Form</tt> component
 * that contains this component) and add standard validation lists to the
 * <tt>properties</tt> parameter, in between squiggly brackets (<tt>{}</tt>).
 * See below for an example.</p>
 *
 * <p>Usage examples:</p>
 * <ul>
 *   <li>Minimal: <tt>&lt;span jwcid="@BeanForm" bean="ognl:pojo"
 *      save="listener:save"/&gt;</tt></li>
 *   <li>More: <tt>&lt;span jwcid="@BeanForm" bean="ognl:pojo"
 *      properties="ognl:'name,email,comment'" save="listener:save"
 *      cancel="listener:cancel" delete="listener:delete"
 *      refresh="listener:refresh" /&gt;</tt></li>
 *   <li>To edit the <tt>comment</tt> property in a text area you would add the
 *      following to your page:<br>
 *      <tt>&lt;div jwcid="commentBeanFieldBlock@Block"&gt;</tt><br>
 *      <tt>&lt;input jwcid="commentBeanField@TextArea"
 *         value="ognl:pojo.comment"
 *         displayName="message:comment"/&gt;</tt><br>
 *      <tt>&lt;/div&gt;</tt><br>
 *      </tt></li>
 *   <li>To make <tt>name</tt> and <tt>email</tt> required, and check that
 *      <tt>email</tt> is actually an email:
 *      <tt>&lt;span jwcid="@BeanForm" bean="ognl:pojo"
 *      properties="ognl:'name{required},email{required,email},comment'"
 *      save="listener:save" clientValidationEnabled="ognl:true"
 *      delegate="bean:validationDelegate"/&gt;</tt></li>
 * </ul>
 *
 * @author Daniel Gredler
 * @version 1.2
 */
public abstract class BeanForm extends BaseComponent {

        private final static Logger LOG = Logger.getLogger( BeanForm.class );
        private final static Pattern PROPERTIES_PATTERN = Pattern.compile( "\\s*(\\w+)\\s*(?:\\{\\s*(.+?)\\s*\\}\\s*)?,?" );
        private final static String CUSTOM_LABEL_BLOCK_SUFFIX = "BeanFieldLabel";        
        private final static String CUSTOM_FIELD_BLOCK_SUFFIX = "BeanFieldBlock";
        private final static String CUSTOM_FIELD_SUFFIX = "BeanField";

        @InjectObject( "infrastructure:requestCycle" )
        public abstract IRequestCycle getCycle();

        @InjectObject( "service:tapestry.form.validator.ValidatorsBindingFactory" )
        public abstract BindingFactory getValidatorsBindingFactory();

        public abstract Object getBean();
        public abstract void setBean( Object bean );

        public abstract String getProperties();
        public abstract void setProperties( String properties );
        
        public abstract boolean isReadOnly();
        public abstract void setReadOnly(boolean value);

        public List<BeanProperty> getBeanProperties() {
                List<BeanProperty> properties = new ArrayList<BeanProperty>();
                Object bean = this.getBean();
                BeanInfo info = this.getBeanInfo();
                PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
                for( PropertyDescriptor descriptor : descriptors ) {
                        BeanProperty property = new BeanProperty( descriptor );
                        String validators = this.getValidators( property );
                        property.setValidators( validators );
                        if( this.isIncluded( property ) ) {
                                if( property.isEditableType() || this.hasCustomField( property ) ) {
                                        properties.add( property );
                                }
                                else if( this.hasExplicitProperties() ) {
                                        LOG.warn( "Explicitly included bean property '" + property.getName() + "' is not a " +
                                                "string/boolean/number/date, and a custom field was not specified for it." );
                                }
                        }
                }
                properties = this.reorderProperties( properties );
                if( LOG.isDebugEnabled() ) {
                        LOG.debug( "Found " + properties.size() + " editable properties for bean " + bean + ": " + properties );
                }
                return properties;
        }

        public boolean isInsideAForm() {
                try {
                        TapestryUtils.getForm( this.getCycle(), this );
                        return true;
                }
                catch( ApplicationRuntimeException e ) {
                        return false;
                }
        }

        @SuppressWarnings("unchecked")
        public List getValidatorList( BeanProperty property, IComponent component ) {
                String name = property.getName();
                String desc = "dynamic validators binding for bean property " + name;
                String expression = property.getValidators();
                Location location = component.getLocation();
                BindingFactory factory = this.getValidatorsBindingFactory();
                ValidatorsBinding binding = (ValidatorsBinding) factory.createBinding( component, desc, expression, location );
                List validators = (List) binding.getObject();
                if( LOG.isDebugEnabled() ) {
                        LOG.debug( "Bean property '" + name + "' has " + validators.size() + " validators: " + validators );
                }
                return validators;
        }
        
        public boolean hasCustomLabel(BeanProperty property) {
        	return getCustomLabelBlock(property) != null;
        }
        public boolean hasCustomField( BeanProperty property ) {
                boolean customBlock = this.getCustomFieldBlock( property ) != null;
                boolean customField = this.getCustomField( property ) != null;
                return customBlock && customField;
        }
        public Block getCustomLabelBlock(BeanProperty property) {
            return (Block) this.getComponent( property, CUSTOM_LABEL_BLOCK_SUFFIX, Block.class );        	
        }
        public Block getCustomFieldBlock( BeanProperty property ) {
                return (Block) this.getComponent( property, CUSTOM_FIELD_BLOCK_SUFFIX, Block.class );
        }

        public IFormComponent getCustomField( BeanProperty property ) {
                return (IFormComponent) this.getComponent( property, CUSTOM_FIELD_SUFFIX, IFormComponent.class );
        }

        @SuppressWarnings( "unchecked" )
        private IComponent getComponent( BeanProperty property, String suffix, Class clazz ) {
                IComponent component = null;
                String name = property.getName() + suffix;
                Map components = this.getPage().getComponents();
                if( components.containsKey( name ) ) {
                        IComponent candidate = (IComponent) components.get( name );
                        if( clazz.isAssignableFrom( candidate.getClass() ) ) {
                                component = candidate;
                        }
                        else {
                                LOG.error( "Component '" + candidate.getId() + "' should be of type " + clazz.getName() + "!" );
                        }
                }
                return component;
        }

        private BeanInfo getBeanInfo() {
                Object bean = this.getBean();
                BeanInfo info;
                if( bean != null ) {
                        try {
                                info = Introspector.getBeanInfo( bean.getClass() );
                        }
                        catch( IntrospectionException e ) {
                                LOG.error( e );
                                info = new SimpleBeanInfo();
                        }
                }
                else {
                        LOG.warn( "BeanForm's bean is null!" );
                        info = new SimpleBeanInfo();
                }
                return info;
        }

        private List<String> explicitPropertyNames;
        private Map<String, String> explicitPropertyValidators;

        private String getValidators( BeanProperty property ) {
                this.initExplicitProperties();
                if( this.explicitPropertyValidators == null ) return null;
                return this.explicitPropertyValidators.get( property.getName() );
        }

        private boolean isIncluded( BeanProperty property ) {
                this.initExplicitProperties();
                if( this.explicitPropertyNames == null ) return true;
                return this.explicitPropertyNames.contains( property.getName() );
        }

        private List<BeanProperty> reorderProperties( List<BeanProperty> properties ) {
                this.initExplicitProperties();
                if( this.explicitPropertyNames == null ) return properties;
                List<BeanProperty> orderedProperties = new ArrayList<BeanProperty>( properties.size() );
                for( String name : this.explicitPropertyNames ) {
                    boolean foundProperty = false;                	
                        for( BeanProperty property : properties ) {
                                if( property.getName().equals( name ) ) {
                                        orderedProperties.add( property );
                                        foundProperty = true;
                                }
                        }
                        // no in the list of bean properties, so consider it a label
                        if (!foundProperty) {
                        	BeanProperty property = new BeanProperty(name);
                        	orderedProperties.add(property);
                        }
                }
                return orderedProperties;
        }

        private boolean hasExplicitProperties() {
                this.initExplicitProperties();
                return ( this.explicitPropertyNames != null );
        }

        private synchronized void initExplicitProperties() {
                if( this.explicitPropertyNames != null ) return;
                String s = this.getProperties();
                if( s == null ) return;
                this.explicitPropertyNames = new ArrayList<String>();
                this.explicitPropertyValidators = new HashMap<String, String>();
                Matcher m = PROPERTIES_PATTERN.matcher( s );
                while( m.find() ) {
                        String name = m.group( 1 );
                        String validators = m.group( 2 );
                        this.explicitPropertyNames.add( name );
                        this.explicitPropertyValidators.put( name, validators );
                }
        }

        /**
         * Instances of this class get serialized into the HTML, so keep it trim (ie,
         * as few serialized instance variables as possible).
         */
        public static class BeanProperty implements Serializable {

                private static final long serialVersionUID = -9064407627959060313L;

                private static final String STRING = String.class.getName();
                private static final String BOOLEAN = Boolean.class.getName();
                private static final String BOOL = boolean.class.getName();
                private static final String INTEGER = Integer.class.getName();
                private static final String INT = int.class.getName();
                private static final String LONG = Long.class.getName();
                private static final String LNG = long.class.getName();
                private static final String FLOAT = Float.class.getName();
                private static final String FLT = float.class.getName();
                private static final String DOUBLE = Double.class.getName();
                private static final String DBL = double.class.getName();
                private static final String DATE = Date.class.getName();

                private String name;
                private String validators;
                private boolean readOnly;
                private String typeName;

                public BeanProperty( PropertyDescriptor descriptor ) {
                        this.name = descriptor.getName();
                        this.validators = null;
                        this.readOnly = descriptor.getWriteMethod() == null;
                        this.typeName = descriptor.getPropertyType().getName();
                }
                
                public BeanProperty(String name) {
                	this.name = name;
                	this.validators = null;
                	this.readOnly = true;
                	this.typeName = null;
                }

                public String getName() {
                        return this.name;
                }

                public String getValidators() {
                        return this.validators;
                }

                public void setValidators( String validators ) {
                        this.validators = validators;
                }

                public boolean isReadOnly() {
                        return this.readOnly;
                }

                public String getTypeName() {
                        return this.typeName;
                }

                public boolean isString() {
                        return STRING.equals( this.typeName );
                }

                public boolean isBoolean() {
                        return BOOLEAN.equals( this.typeName ) || BOOL.equals( this.typeName );
                }

                public boolean isNumber() {
                        return INTEGER.equals( this.typeName ) || INT.equals( this.typeName ) ||
                                LONG.equals( this.typeName ) || LNG.equals( this.typeName ) ||
                                FLOAT.equals( this.typeName ) || FLT.equals( this.typeName ) ||
                                DOUBLE.equals( this.typeName ) || DBL.equals( this.typeName );
                }

                public boolean isDate() {
                        return DATE.equals( this.typeName );
                }

                public boolean isEditableType() {
                        return this.isString() || this.isBoolean() || this.isNumber() || this.isDate();
                }

                @Override
                public String toString() {
                        return this.name + ( this.validators != null ? "{" + this.validators + "}" : "" );
                }

        }

}