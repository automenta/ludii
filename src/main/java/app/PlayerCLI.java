// 
// Decompiled by Procyon v0.5.36
// 

package app;

import expert_iteration.ExpertIteration;
import main.CommandLineArgParse;
import supplementary.experiments.eval.EvalAgents;
import supplementary.experiments.eval.EvalGate;
import supplementary.experiments.scripts.GenerateGatingScripts;
import supplementary.experiments.speed.PlayoutsPerSec;

import java.util.Arrays;

public class PlayerCLI
{
    public static void runCommand(final String[] args) {
        final String[] commandArg = { args[0] };
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "Run one of Ludii's command-line options, followed by the command's arguments.\nEnter a command's name followed by \"-h\" or \"--help\" for more information on the arguments for that particular command.");
        argParse.addOption(new CommandLineArgParse.ArgOption().help("Command to run.").setRequired().withLegalVals("--time-playouts", "--expert-iteration", "--eval-agents", "--find-crashing-trial", "--eval-gate", "--generate-gating-scripts").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        if (!argParse.parseArguments(commandArg)) {
            return;
        }
        final String command = argParse.getValueString(0);
        final String[] passArgs = Arrays.copyOfRange(args, 1, args.length);
        if (command.equalsIgnoreCase("--time-playouts")) {
            PlayoutsPerSec.main(passArgs);
        }
        else if (command.equalsIgnoreCase("--expert-iteration")) {
            ExpertIteration.main(passArgs);
        }
        else if (command.equalsIgnoreCase("--eval-agents")) {
            EvalAgents.main(passArgs);
        }
//        else if (command.equalsIgnoreCase("--find-crashing-trial")) {
//            FindCrashingTrial.main(passArgs);
//        }
        else if (command.equalsIgnoreCase("--eval-gate")) {
            EvalGate.main(passArgs);
        }
        else if (command.equalsIgnoreCase("--generate-gating-scripts")) {
            GenerateGatingScripts.main(passArgs);
        }
        else {
            System.err.println("ERROR: command not yet implemented: " + command);
        }
    }
}
