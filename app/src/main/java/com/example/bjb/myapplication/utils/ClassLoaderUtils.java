/**
 * 
 */
package com.example.bjb.myapplication.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassLoaderUtils {
	
	/***
	 * Returns complete class path from all available <code>URLClassLoaders</code>
	 * starting from class loader that has loaded the specified class. 
	 */
	public static URL[] getFullClassPath(Class<?> clazz) {
		return getFullClassPath(clazz.getClassLoader());
	}

	/***
	 * Returns complete class path from all available <code>URLClassLoader</code>s.
	 */
	public static URL[] getFullClassPath(ClassLoader classLoader) {
		List<URL> list = new ArrayList<URL>();
		while (classLoader != null) {
			if (classLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) classLoader).getURLs();
				list.addAll(Arrays.asList(urls));
			}
			classLoader = classLoader.getParent();
		}

		URL[] result = new URL[list.size()];
		return list.toArray(result);
	}


	// ---------------------------------------------------------------- get resource

	/***
	 * Retrieves given resource as URL.
	 * @see #getResourceUrl(String, Class)
	 */
	public static URL getResourceUrl(String resourceName) {
		return getResourceUrl(resourceName, null);
	}

	/***
	 * Retrieves given resource as URL.
	 * <p>
	 * Resource will be loaded using class loaders in the following order:
	 * <ul>
	 * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
	 * <li>{@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}
	 * <li>if <code>callingClass</code> is provided: {@link Class#getClassLoader() callingClass.getClassLoader()}
	 * </ul>
	 */
	public static URL getResourceUrl(String resourceName, Class<?> callingClass) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
		if (url == null) {
			url = ClassLoaderUtils.class.getClassLoader().getResource(resourceName);
		}
		if ((url == null) && (callingClass != null)) {
			ClassLoader cl = callingClass.getClassLoader();
			if (cl != null) {
				url = cl.getResource(resourceName);
			}
		}
		return url;
	}

	// ---------------------------------------------------------------- get resource file

	/***
	 * Retrieves resource as file.
	 * @see #getResourceFile(String)
	 */
	public static File getResourceFile(String resourceName) {
		return getResourceFile(resourceName, null);
	}

	/***
	 * Retrieves resource as file. Resource is retrieved as {@link #getResourceUrl(String, Class) URL},
	 * than it is converted to URI so it can be used by File constructor.
	 */
	public static File getResourceFile(String resourceName, Class<?> callingClass) {
		try {
			return new File(getResourceUrl(resourceName, callingClass).toURI());
		} catch (URISyntaxException usex) {
			return null;
		}
	}

	// ---------------------------------------------------------------- get resource stream

	/***
	 * Opens a resource of the specified name for reading.
	 * @see #getResourceAsStream(String, Class)
	 */
	public static InputStream getResourceAsStream(String resourceName) throws IOException {
		return getResourceAsStream(resourceName, ClassLoaderUtils.class);
	}

	/***
	 * Opens a resource of the specified name for reading.
	 * @see #getResourceUrl(String, Class)
	 */
	public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) throws IOException {
		URL url = getResourceUrl(resourceName, callingClass);
		if (url != null) {
			return url.openStream();
		}
		return null;
	}

	/***
	 * Opens a class of the specified name for reading.
	 * @see #getResourceAsStream(String, Class)
	 */
	public static InputStream getClassAsStream(Class<?> clazz) throws IOException {
		return getResourceAsStream(getClassFileName(clazz), clazz);
	}

	/***
	 * Opens a class of the specified name for reading.
	 * @see #getResourceAsStream(String, Class)
	 */
	public static InputStream getClassAsStream(String className) throws IOException {
		return getResourceAsStream(getClassFileName(className), ClassLoaderUtils.class);
	}


	// ---------------------------------------------------------------- load class

	/***
	 * Loads a class with a given name dynamically.
	 * @see #loadClass(String, Class)
	 */
	public static Class<?> loadClass(String className) throws ClassNotFoundException {
		return loadClass(className, null);
	}

	/***
	 * Loads a class with a given name dynamically, more reliable then <code>Class.forName</code>.
	 * <p>
	 * Class will be loaded using class loaders in the following order:
	 * <ul>
	 * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
	 * <li>the basic {@link Class#forName(java.lang.String)}
	 * <li>{@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}
	 * <li>if <code>callingClass</code> is provided: {@link Class#getClassLoader() callingClass.getClassLoader()}
	 * </ul>
	 */
	public static Class<?> loadClass(String className, Class<?> callingClass) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException cnfex1) {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException cnfex2) {
				try {
					return ClassLoaderUtils.class.getClassLoader().loadClass(className);
				} catch (ClassNotFoundException cnfex3) {
					if (callingClass != null) {
						return callingClass.getClassLoader().loadClass(className);
					}
					throw cnfex3;
				}
			}
		}
	}

	// ---------------------------------------------------------------- misc


	/***
	 * Resolves class file name from class name by replacing dot's with '/' separator
	 * and adding class extension at the end.
	 */
	public static String getClassFileName(Class<?> clazz) {
		if (clazz.isArray()) {
			clazz = clazz.getComponentType();
		}
		return getClassFileName(clazz.getName());
	}

	/***
	 * Resolves class file name from class name by replacing dot's with '/' separator.
	 */
	public static String getClassFileName(String className) {
		return className.replace('.', '/') + ".class";
	}

}
