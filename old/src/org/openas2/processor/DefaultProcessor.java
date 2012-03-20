package org.openas2.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openas2.OpenAS2Exception;
import org.openas2.message.Message;


public class DefaultProcessor extends BaseProcessor {
    private List modules;

    public List getActiveModules() {
        List activeMods = new ArrayList();
        Iterator moduleIt = getModules().iterator();
        ProcessorModule procMod;

        while (moduleIt.hasNext()) {
            procMod = (ProcessorModule) moduleIt.next();

            if (procMod instanceof ActiveModule) {
                activeMods.add(procMod);
            }
        }

        return activeMods;
    }

    public void setModules(List modules) {
        this.modules = modules;
    }

    public List getModules() {
        if (modules == null) {
            modules = new ArrayList();
        }

        return modules;
    }

    public void handle(String action, Message msg, Map options)
        throws OpenAS2Exception {
        Iterator moduleIt = getModules().iterator();
        ProcessorModule module;
        ProcessorException pex = null;
        boolean moduleFound = false;

        while (moduleIt.hasNext()) {
            module = (ProcessorModule) moduleIt.next();

            if (module.canHandle(action, msg, options)) {
                try {
                    moduleFound = true;
                    module.handle(action, msg, options);
                } catch (OpenAS2Exception oae) {
                    if (pex == null) {
                        pex = new ProcessorException(this);
                        pex.getCauses().add(oae);
                    }
                }
            }
        }

        if (pex != null) {
            throw pex;
        } else if (!moduleFound) {
            throw new NoModuleException(action, msg, options);
        }
    }

    public void startActiveModules() {
        Iterator activeIt = getActiveModules().iterator();

        while (activeIt.hasNext()) {
            try {
                ((ActiveModule) activeIt.next()).start();
            } catch (OpenAS2Exception e) {
                e.terminate();
            }
        }
    }

    public void stopActiveModules() {
        Iterator activeIt = getActiveModules().iterator();

        while (activeIt.hasNext()) {
            try {
                ((ActiveModule) activeIt.next()).stop();
            } catch (OpenAS2Exception e) {
                e.terminate();
            }
        }
    }
}
