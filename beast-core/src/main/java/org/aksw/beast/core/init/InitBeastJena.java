package org.aksw.beast.core.init;

import org.apache.jena.system.JenaSubsystemLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitBeastJena
    implements JenaSubsystemLifecycle
{
    private static final Logger logger = LoggerFactory.getLogger(InitBeastJena.class);

    public void start() {
        logger.debug("Beast Jena initialization");
        // Not sure if we need to register custom Resource Types - if not, we can eventually remove this class
        // The issue with ResourceData is, that its GenericType
        // BuiltinPersonalities.model.add(p)
    }

    @Override
    public void stop() {
    }
}
