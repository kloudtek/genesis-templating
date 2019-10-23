package com.aeontronix.genesis.step;

import java.util.Map;

public interface StepProvider {
    Map<String, Class<? extends Step>> getSteps();
}
