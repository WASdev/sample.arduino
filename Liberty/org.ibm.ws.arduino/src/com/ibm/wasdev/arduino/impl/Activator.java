/**
 * (C) Copyright IBM Corporation 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.wasdev.arduino.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

public class Activator implements BundleActivator, ManagedServiceFactory {

    private ServiceRegistration configRef;
    private final Map<String, String> keyToId = new ConcurrentHashMap<String, String>();

    @Override
    public void start(BundleContext context) throws Exception {
        this.configRef = context.registerService(
                ManagedServiceFactory.class.getName(), this, getDefaults());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        ServiceManager.closeAll();
        configRef.unregister();
    }

    private Dictionary<String, String> getDefaults() {
        Dictionary<String, String> defaults = new Hashtable<String, String>();
        defaults.put(org.osgi.framework.Constants.SERVICE_PID, "arduino");
        return defaults;
    }

    @Override
    public void deleted(String key) {
        String id = keyToId.remove(key);
        ServiceManager.remove(id);

    }

    @Override
    public String getName() {
        return "Arduino";
    }

    @Override
    public void updated(String key, Dictionary<String, ?> config) throws ConfigurationException {
        String id = (String) config.get("id");
        keyToId.put(key, id);
        ServiceManager.update(id, config);
    }

}