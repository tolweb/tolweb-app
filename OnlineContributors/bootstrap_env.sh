#!/bin/sh



if [ ! -d ~/.tolweb-configs ]; then
    echo "You must set '~/.tolweb-configs' in order to build the project." 
fi

cp -v ~/.tolweb-configs/OnlineContributors/db*.properties ./src/ && echo "Bootstrapping of configuration values for environment complete..."
