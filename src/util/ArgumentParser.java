package util;

import java.io.File;

public class ArgumentParser {
    private File mainSrcFile;
    private boolean allValid;
    private String msg;
    private String[] splArgs;

    public ArgumentParser(String[] args) {
        parseArgs(args);
    }

    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (mainSrcFile == null) {
                mainSrcFile = new File(args[i]);
                if (!mainSrcFile.exists()) {
                    msg = "Source file does not exist";
                    allValid = false;
                    return;
                }
            } else {
                splArgs = new String[args.length - i];
                System.arraycopy(args, i, splArgs, 0, splArgs.length);
                break;
            }
        }
        allValid = true;
    }

    public File getMainSrcFile() {
        return mainSrcFile;
    }

    public boolean isAllValid() {
        return allValid;
    }

    public boolean importLang() {
        return true;
    }

    public String getMsg() {
        return msg;
    }

    public String[] getSplArgs() {
        return splArgs;
    }
}
