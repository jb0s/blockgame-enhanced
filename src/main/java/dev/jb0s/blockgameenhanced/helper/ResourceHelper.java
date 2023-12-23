package dev.jb0s.blockgameenhanced.helper;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceHelper {

    /**
     * Export a resource embedded into a Jar file to the local file path.
     * Pasted from https://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar #Skidding
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    public static String exportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = ResourceHelper.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(ResourceHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return jarFolder + resourceName;
    }

    /**
     * Also pasted: https://www.digitalocean.com/community/tutorials/java-unzip-file-example
     */
    public static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);

        // Create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;

        // Buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();

                if(ze.isDirectory()) {
                    Files.createDirectory(Path.of(destDir + File.separator + fileName));
                }
                else {
                    File newFile = new File(destDir + File.separator + fileName);
                    BlockgameEnhanced.LOGGER.info("Extracting " + fileName + " to " + newFile.getAbsolutePath());

                    // Create directories for sub directories in zip
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }

                // Close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            // Close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
