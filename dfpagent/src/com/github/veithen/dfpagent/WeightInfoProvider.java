package com.github.veithen.dfpagent;

/**
 * Provides a collection of {@link WeightInfo} objects to an {@link Agent}.
 */
public interface WeightInfoProvider {
    /**
     * Get the current weight information for all addresses and ports. This method is called by the
     * {@link Agent} every time weight information is sent to the DFP Manager.
     * 
     * @return the weight information
     */
    WeightInfo[] getWeightInfo();
}
