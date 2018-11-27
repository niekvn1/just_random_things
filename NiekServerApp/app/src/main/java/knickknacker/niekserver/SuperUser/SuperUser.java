package knickknacker.niekserver.SuperUser;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class SuperUser {
    public static boolean ls(String path) {
        File file = new File(path);
        String filePath = file.getAbsolutePath();
        String[] rights = new String[]{"/system/bin/ls", "-l", filePath};
        return runCommand(rights);
    }

    public static boolean chmod(String path, int code) {
        File file = new File(path);
        String filePath = file.getAbsolutePath();
        return su(new String[] {"chmod", Integer.toString(code), filePath});
    }

    public static boolean mkdir(String path) {
        return runCommand(new String[] {"mkdir", path});
    }

    public static boolean copy(String path, String directory) {
        File file = new File(path);
        String filePath = file.getAbsolutePath();
        return true;
    }

    public static String exec(String path) {
        StreamOutput streamOutput;
        try {
            Process process = Runtime.getRuntime().exec("sh " + path);
            streamOutput = new StreamOutput();
            stringStream(process.getInputStream(), streamOutput);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return streamOutput.getString();
    }

    public static HashMap<String, StreamOutput> python(String python, String env, String args) {
        HashMap<String, StreamOutput> streamOutputs = new HashMap<>();
        StreamOutput error;
        try {
            Log.i("ARGS", args);
            Process process = Runtime.getRuntime().exec("sh");
            DataOutputStream out = new DataOutputStream(process.getOutputStream());
            out.writeBytes(". " + env + "\n");
            out.writeBytes(python + " " + args + "\n");

            out.writeBytes("exit\n");

            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String key = null;
            try {
                while ((line = in.readLine()) != null) {
                    if (line.equals("#[QPython] Press enter to exit")) {
                        out.writeBytes("\n");
                        out.writeBytes("exit\n");
                        out.flush();
                    } else if (line.length() >= 11 && line.substring(0, 11).equals("@NiekServer")) {
                        String[] split = line.split(":");
                        if (split.length == 2) {
                            key = split[1];
                            if (!streamOutputs.containsKey(key)) {
                                streamOutputs.put(key, new StreamOutput());
                            }
                        }
                    } else if (key != null) {
                        streamOutputs.get(key).append(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            error = logStream(process.getErrorStream(), "PYTHON_ERROR");

            process.waitFor();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        if (error != null) {
            streamOutputs.put("ERROR", error);
        }

        return streamOutputs;
    }

    private static boolean su(String[] command) {
        try {
            Process process = Runtime.getRuntime().exec("su");

            StringBuilder stringBuilder = new StringBuilder();
            for (String string : command) {
                stringBuilder.append(string);
                stringBuilder.append(" ");
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("\n");
            Log.i("su", stringBuilder.toString());

            DataOutputStream out = new DataOutputStream(process.getOutputStream());
            out.writeBytes(stringBuilder.toString());
            out.writeBytes("exit\n");
            out.flush();
            process.waitFor();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean runCommand(String[] command) {
        try {
            Process pro = Runtime.getRuntime().exec(command);
            logStream(pro.getInputStream(), "STREAM");
            logStream(pro.getErrorStream(), "ERROR");
            pro.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static StreamOutput logStream(InputStream stream, String name) {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StreamOutput streamOutput = null;
        boolean first = true;
        try {
            while ((line = in.readLine()) != null) {
                if (first) {
                    streamOutput = new StreamOutput();
                    first = false;
                }

                Log.i(name, line);
                streamOutput.append(line);
                streamOutput.append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return streamOutput;
    }

    private static void stringStream(InputStream stream, StreamOutput output) {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        try {
            while ((line = in.readLine()) != null) {
                Log.i("StringStream", line);
                output.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class StreamOutput {
        StringBuilder stringBuilder;

        private StreamOutput() {
            stringBuilder = new StringBuilder();
        }

        private void append(String string) {
            stringBuilder.append(string);
        }

        public String getString() {
            return stringBuilder.toString();
        }
    }
}
