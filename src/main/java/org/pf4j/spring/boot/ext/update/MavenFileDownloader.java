/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.pf4j.spring.boot.ext.update;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.pf4j.PluginException;
import org.pf4j.update.FileDownloader;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;
import org.springframework.cloud.deployer.resource.maven.MavenResource;

public class MavenFileDownloader implements FileDownloader {

	private MavenProperties mavenProperties;
	
	public MavenFileDownloader(MavenProperties mavenProperties) {
		this.mavenProperties = mavenProperties;
	}

	/**
     * Downloads a file. If HTTP(S) or FTP, stream content, if local file:/ do a simple filesystem copy to tmp folder.
     * Other protocols not supported.
     * @param fileUrl the URI representing the file to download
     * @return the path of downloaded/copied file
     * @throws IOException in case of network or IO problems
     * @throws PluginException in case of other problems
     */
    public Path downloadFile(URL fileUrl) throws PluginException, IOException {
        switch (fileUrl.getProtocol()) {
	        case "http":
	        case "https":
	        case "ftp":
                return downloadFileHttp(fileUrl);
            default:
                throw new PluginException("URL protocol {} not supported", fileUrl.getProtocol());
        }
    }
    
    /**
     * Downloads file from HTTP or FTP
     * @param fileUrl source file
     * @return path of downloaded file
     * @throws IOException if IO problems
     * @throws PluginException if validation fails or any other problems
     */
    protected Path downloadFileHttp(URL fileUrl) throws IOException, PluginException {
        Path destination = Files.createTempDirectory("pf4j-update-downloader");
        destination.toFile().deleteOnExit();
        
        String path = fileUrl.getPath();
        String coordinates = path.substring(path.lastIndexOf('/') + 1);
        
        return MavenResource.parse(coordinates, mavenProperties).getFile().toPath();
    }
 
    
}
