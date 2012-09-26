/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.eti.kinoshita.libreoffice.spellcheck;

import java.util.Arrays;
import java.util.List;

import ooo.connector.BootstrapSocketConnector;
import ooo.connector.server.OOoServer;

import com.sun.star.beans.PropertyValue;
import com.sun.star.lang.Locale;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.linguistic2.XLinguServiceManager;
import com.sun.star.linguistic2.XSpellChecker;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;


/**
 * An example of how to use headless libreoffice for spell checking.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class Example {

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    public static void main(String[] args) throws Exception {
        // Start headless libreoffice
        List oooOptions = OOoServer.getDefaultOOoOptions();
        oooOptions.add("--nofirststartwizard");
        oooOptions.add("--headless");
        OOoServer oooServer = new OOoServer("/usr/lib/libreoffice/program/soffice", oooOptions);
        BootstrapSocketConnector bootstrapSocketConnector = new BootstrapSocketConnector(oooServer);
        //context = Bootstrap.bootstrap();
        XComponentContext context = bootstrapSocketConnector.connect();
        
        // Create component factory
        XMultiComponentFactory serviceManager = context.getServiceManager();
        Object aObj = serviceManager.createInstanceWithContext("com.sun.star.linguistic2.LinguServiceManager", context);
        XLinguServiceManager mxLinguSvcMgr = (XLinguServiceManager) UnoRuntime.queryInterface(XLinguServiceManager.class, aObj);
        
        // Le spell checker
        XSpellChecker spellChecker = mxLinguSvcMgr.getSpellChecker();
        
        // The locale you want to spell check
        Locale locale = new Locale("en", "US", "");
        
        if(!spellChecker.hasLocale(locale)) {
            System.err.println("Locale not supported");
            System.exit(-1);
        }
        
        // Boilerplate code
        PropertyValue[] aEmptyProps = new PropertyValue[0];
        String[] input = Arrays.asList("analyse", "analyze", "centre", "center").toArray(new String[0]);
        for(String word : input) {
            // Check whether it is a valid token or not
            boolean r = spellChecker.isValid(word, locale, aEmptyProps);
            if(!r)
                System.out.println(word);
        }
        
        // Outputs: analyse\ncentre
        
        // Important to kill LibreOffice
        System.exit(1); 
        
    }
    
}
