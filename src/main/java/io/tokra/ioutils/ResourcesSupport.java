package io.tokra.ioutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ResourcesSupport {
	
	private ResourcesSupport() {
	}
	
	public static URL getResourceURL(String pathInClasspaths) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		if (cl != null) {
			return cl.getResource(pathInClasspaths);
		}
		throw new RuntimeException("Could not get ClassLoader !");
	}
	
	public static URI getResourceURI(String pathInClasspaths) throws IOException, URISyntaxException {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		if (cl != null) {
			URL url = cl.getResource(pathInClasspaths);
			if (url != null) {
				return url.toURI();
			}
			throw new IOException("Could not find: " + pathInClasspaths);
		}
		throw new RuntimeException("Could not get ClassLoader !");
	}
	
	public static InputStream getResourceAsStream(String pathInClasspaths) throws IOException {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		if (cl != null) {
			return cl.getResourceAsStream(pathInClasspaths);
		}
		throw new RuntimeException("Could not get ClassLoader !");
	}
	
	public static File getResourceAsFile(String pathInClasspaths) throws IOException, URISyntaxException {
		URL url = getResourceURL(pathInClasspaths);
		if (url != null) {
			return new File(url.toURI());
		}
		throw new IOException("URL was null !");
	}
	
	public static Path getResourceAsPath(String pathInClasspaths) throws IOException {
		URL url = getResourceURL(pathInClasspaths);
		if (url != null) {
			try {
				return Paths.get(getResourceURI(pathInClasspaths));
			} catch (URISyntaxException e) {
				throw new IOException(e);
			}
		}
		throw new IOException("URL was null !");
	}

}