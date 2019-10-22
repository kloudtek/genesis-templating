package com.kloudtek.genesis.step;

import java.util.Map;

public interface StepProvider {
    Map<String, Class<? extends Step>> getSteps();
}
