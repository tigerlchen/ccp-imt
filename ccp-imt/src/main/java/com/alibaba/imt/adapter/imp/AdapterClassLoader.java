package com.alibaba.imt.adapter.imp;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class AdapterClassLoader extends ClassLoader {
	static private final ArrayList<AdapterClassLoader> adapterClassLoaders = new ArrayList<AdapterClassLoader>();

	static AdapterClassLoader get (Class<?> type) {
		ClassLoader parent = type.getClassLoader();
		synchronized (adapterClassLoaders) {
			for (int i = 0, n = adapterClassLoaders.size(); i < n; i++) {
				AdapterClassLoader adapterClassLoader = adapterClassLoaders.get(i);
				if (adapterClassLoader.getParent() == parent) return adapterClassLoader;
			}
			AdapterClassLoader adapterClassLoader = new AdapterClassLoader(parent);
			adapterClassLoaders.add(adapterClassLoader);
			return adapterClassLoader;
		}
	}

	private AdapterClassLoader (ClassLoader parent) {
		super(parent);
	}

	@Override
	protected synchronized java.lang.Class<?> loadClass (String name, boolean resolve) throws ClassNotFoundException {

		return super.loadClass(name, resolve);
	}

	Class<?> defineClass (String name, byte[] bytes) throws ClassFormatError {
		try {
			// Attempt to load the access class in the same loader, which makes protected and default access members accessible.
			Method method = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] {String.class, byte[].class, int.class,
				int.class});
			method.setAccessible(true);
			return (Class<?>)method.invoke(getParent(), new Object[] {name, bytes, Integer.valueOf(0), Integer.valueOf(bytes.length)});
		} catch (Exception ignored) {
		}
		return defineClass(name, bytes, 0, bytes.length);
	}
}
