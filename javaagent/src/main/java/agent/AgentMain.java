/**
 * Copyright:Copyright (c) 2021
 * Create By Jimmy
 * Create On 2021-21.7.12 at 1:03
 **/
package agent;

import java.lang.instrument.Instrumentation;

public class AgentMain  {
    public static void premain(String agentArgs, Instrumentation inst) {
        final SimpleTransformer transformer = new SimpleTransformer();
        inst.addTransformer(transformer);
    }

}
