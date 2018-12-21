package com.melot.engine;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AndroidUtils {

    private long lastTotalSize = 0;
    private long lastTime = 0;
    private long lastTotalCpu = 0;
    private long lastProcessCpu = 0;

    private byte[] pictureBuf;

    public void loadImageBuf(Context context) {
        try {
            //360 * 640 * 4
            InputStream in = context.getResources().getAssets().open("360x640.rgb32");
            int length = in.available();
            Log.e("test", "length = " + length);
            pictureBuf = new byte[length];
            in.read(pictureBuf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getNetTransmit() {
        long val = 0;
        long now = System.currentTimeMillis();
        long totalSize = getTotalBytes();
        if (lastTime != 0) {
            val = (totalSize - lastTotalSize) / (long) (now - lastTime);   //k/s
        }
        //Log.e("NetSpeed", "getPerTransmit totalsize = " + totalSize + "   time = " + now + "    val = " + val);
        lastTime = now;
        lastTotalSize = totalSize;
        return val;
    }

    public long getProcessCpuUsed() {
        long pcpu = 0;
        long nowTotalCpu = getTotalCpu();
        long nowProcessCpu = getMyProcessCpu();
        if (nowTotalCpu != 0) {
            pcpu = 100 * (nowProcessCpu - lastProcessCpu) / (nowTotalCpu - lastTotalCpu);
        }
        lastProcessCpu = nowProcessCpu;
        lastTotalCpu = nowTotalCpu;
        //Log.e("NetSpeed", "getProcessCpuUsed cpu = " + pcpu);
        return pcpu;
    }

    public long getTotalBytes() {
        FileReader fr = null;
        String line = "";
        int index = 0;
        String[] segs;
        boolean isNum;
        long tmp = 0;
        long totalSize = 0;
        try {
            fr = new FileReader("/proc/net/dev");
            BufferedReader in = new BufferedReader(fr, 500);
            while ((line = in.readLine()) != null) {
                line = line.trim();
                index = 0;
                if (line.startsWith("rmnet") || line.startsWith("eth") || line.startsWith("wlan") || line.startsWith("ccmni")) {
                    segs = line.split(":")[1].split(" +");
                    for (int i = 0; i < segs.length; i++) {
                        isNum = true;
                        try {
                            tmp = Long.parseLong(segs[i]);
                        } catch (Exception e) {
                            isNum = false;
                        }
                        if (isNum == true) {
                            index++;
                        }
                        if (index == 9) {
                            totalSize = totalSize + tmp;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSize;
    }

    private long getTotalCpu() {
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            Log.e("", "IOException" + ex.toString());
            return 0;
        }
        long totalCpu = 0;
        try {
            totalCpu = Long.parseLong(cpuInfos[2])
                    + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                    + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                    + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i("", "ArrayIndexOutOfBoundsException" + e.toString());
            return 0;
        }
        return totalCpu;
    }

    private long getMyProcessCpu() {
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException e) {
            Log.e("", "IOException" + e.toString());
            return 0;
        }
        long appCpuTime = 0;
        try {
            appCpuTime = Long.parseLong(cpuInfos[13])
                    + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                    + Long.parseLong(cpuInfos[16]);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.i("", "ArrayIndexOutOfBoundsException" + e.toString());
            return 0;
        }
        return appCpuTime;
    }

    public byte[] getPictureBuf() {
        return pictureBuf;
    }

}
