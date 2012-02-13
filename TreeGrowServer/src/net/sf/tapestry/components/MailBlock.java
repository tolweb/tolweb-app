//Copyright 2004 Adam Greene
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package net.sf.tapestry.components;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.markup.AsciiMarkupFilter;
import org.apache.tapestry.markup.MarkupWriterImpl;
import org.tolweb.tapestry.injections.BaseInjectable;

public abstract class MailBlock extends AbstractComponent implements BaseInjectable {
	@Parameter(defaultValue = "'dmandel@tolweb.org'")
	public abstract String getFrom();
	@Parameter
	public abstract Collection getTo();
	@Parameter
	public abstract String getSubject();
	@Parameter
	public abstract boolean getHtml();

	/**
	 * @see net.sf.tapestry.AbstractComponent#renderComponent(IMarkupWriter, IRequestCycle)
	 */
	protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		IMarkupWriter subwriter = new MarkupWriterImpl("text/plain", new PrintWriter(bout), new AsciiMarkupFilter());
		renderBody(subwriter, cycle);
		subwriter.flush();
		String body = new String(bout.toByteArray());
		try {
			doMail(body);
		} catch (Exception e) {
			//System.out.println("Could send email titled \"" + getSubject() + "\":");
			//e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param body the content of the email body
	 * @throws Exception
	 */
	protected void doMail(String body) throws Exception {

		// Get system properties
		Properties props = System.getProperties();
		// Setup mail server
		props.put("mail.smtp.host", getConfiguration().getSmtpHost());
		// Get session
		Session session = Session.getDefaultInstance(props, null);

		// Define message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(getFrom()));

		Iterator i = getTo().iterator();
		// Load the recipients into the mail message
		while (i.hasNext()) {
			message.addRecipient(
				Message.RecipientType.TO,
				new InternetAddress("" + i.next()));
		}

		// Set the subject of the email
		message.setSubject(getSubject());
		
		if (getHtml()) {
			message.setContent(body, "text/html");
		} else {
			message.setContent(body, "text/plain");
		}
		// Send message
		Transport.send(message);
	}
}