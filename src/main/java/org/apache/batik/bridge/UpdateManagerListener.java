// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public interface UpdateManagerListener
{
    void managerStarted(final UpdateManagerEvent p0);
    
    void managerSuspended(final UpdateManagerEvent p0);
    
    void managerResumed(final UpdateManagerEvent p0);
    
    void managerStopped(final UpdateManagerEvent p0);
    
    void updateStarted(final UpdateManagerEvent p0);
    
    void updateCompleted(final UpdateManagerEvent p0);
    
    void updateFailed(final UpdateManagerEvent p0);
}
