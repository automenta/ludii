// 
// Decompiled by Procyon v0.5.36
// 

package app;

public class StartApp
{
    public static void main(final String[] args) {
        if (args.length == 0) {
            final DesktopApp app = new DesktopApp();
            app.createPlayerApp();
        }
        else {
            PlayerCLI.runCommand(args);
        }
    }
}
