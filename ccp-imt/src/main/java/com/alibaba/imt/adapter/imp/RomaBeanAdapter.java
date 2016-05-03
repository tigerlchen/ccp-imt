package com.alibaba.imt.adapter.imp;

import com.alibaba.imt.adapter.BeanAdapter;
import com.alibaba.imt.asm.ClassWriter;
import com.alibaba.imt.asm.Label;
import com.alibaba.imt.asm.MethodVisitor;
import com.alibaba.imt.asm.Opcodes;
import com.alibaba.imt.asm.Type;


public class RomaBeanAdapter implements BeanAdapter,Opcodes {

	private static RomaBeanAdapter adapterInstance = null;
	
	static{
	    AdapterClassLoader adapterClassLoader = AdapterClassLoader.get(RomaBeanAdapter.class);
        Class<?> subAdapterClass = null;
        String adapterClassName = RomaBeanAdapter.class.getName();
        String subAdapterClassName = adapterClassName + "_subClass";
		synchronized (adapterClassLoader) {
			try {
				subAdapterClass = adapterClassLoader.loadClass(subAdapterClassName);
			} catch (ClassNotFoundException ignored) {
			    ClassWriter cw = new ClassWriter(0);
		        MethodVisitor mv;
		        
		        String adapterInternalName = Type.getInternalName(RomaBeanAdapter.class);
		        String subAdapterInternalName = adapterInternalName + "_subClass";
		        String adapterDesc = Type.getDescriptor(RomaBeanAdapter.class);
		        String subAdapterDesc = adapterDesc.replaceFirst("\\;", "_subClass;");
		        
		        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, subAdapterInternalName, null, adapterInternalName, null);
		        
		        {
		        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		        mv.visitCode();
		        Label l0 = new Label();
		        mv.visitLabel(l0);
		        mv.visitLineNumber(37, l0);
		        mv.visitVarInsn(ALOAD, 0);
		        mv.visitMethodInsn(INVOKESPECIAL, adapterInternalName, "<init>", "()V");
		        mv.visitInsn(RETURN);
		        Label l1 = new Label();
		        mv.visitLabel(l1);
		        mv.visitLocalVariable("this", subAdapterDesc, null, l0, l1, 0);
		        mv.visitMaxs(1, 1);
		        mv.visitEnd();
		        }
		        {
		        mv = cw.visitMethod(ACC_PUBLIC, "getInstance", "(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;", "<T:Ljava/lang/Object;>(Ljava/lang/Class<TT;>;[Ljava/lang/Object;)TT;", null);
		        mv.visitCode();
		        Label l0 = new Label();
		        mv.visitLabel(l0);
		        mv.visitLineNumber(40, l0);
		        mv.visitLdcInsn(Type.getType("Lorg/springframework/context/ApplicationContext;"));
		        mv.visitMethodInsn(INVOKESTATIC, "com/alibaba/roma/web/util/ApplicationContextUtils", "get", "(Ljava/lang/Class;)Ljava/lang/Object;");
		        mv.visitTypeInsn(CHECKCAST, "org/springframework/context/ApplicationContext");
		        mv.visitVarInsn(ASTORE, 3);
		        Label l1 = new Label();
		        mv.visitLabel(l1);
		        mv.visitLineNumber(41, l1);
		        mv.visitVarInsn(ALOAD, 3);
		        mv.visitVarInsn(ALOAD, 1);
		        mv.visitMethodInsn(INVOKEINTERFACE, "org/springframework/context/ApplicationContext", "getBeansOfType", "(Ljava/lang/Class;)Ljava/util/Map;");
		        mv.visitVarInsn(ASTORE, 4);
		        Label l2 = new Label();
		        mv.visitLabel(l2);
		        mv.visitLineNumber(42, l2);
		        mv.visitInsn(ACONST_NULL);
		        mv.visitVarInsn(ASTORE, 5);
		        Label l3 = new Label();
		        mv.visitLabel(l3);
		        mv.visitLineNumber(43, l3);
		        mv.visitVarInsn(ALOAD, 2);
		        Label l4 = new Label();
		        mv.visitJumpInsn(IFNULL, l4);
		        mv.visitVarInsn(ALOAD, 2);
		        mv.visitInsn(ARRAYLENGTH);
		        mv.visitJumpInsn(IFLE, l4);
		        Label l5 = new Label();
		        mv.visitLabel(l5);
		        mv.visitLineNumber(44, l5);
		        mv.visitVarInsn(ALOAD, 2);
		        mv.visitInsn(ICONST_0);
		        mv.visitInsn(AALOAD);
		        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
		        mv.visitVarInsn(ASTORE, 6);
		        Label l6 = new Label();
		        mv.visitLabel(l6);
		        mv.visitLineNumber(45, l6);
		        mv.visitVarInsn(ALOAD, 4);
		        mv.visitVarInsn(ALOAD, 6);
		        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
		        mv.visitVarInsn(ASTORE, 5);
		        mv.visitLabel(l4);
		        mv.visitLineNumber(47, l4);
		        mv.visitFrame(Opcodes.F_APPEND,3, new Object[] {"org/springframework/context/ApplicationContext", "java/util/Map", "java/lang/Object"}, 0, null);
		        mv.visitVarInsn(ALOAD, 5);
		        mv.visitInsn(ARETURN);
		        Label l7 = new Label();
		        mv.visitLabel(l7);
		        mv.visitLocalVariable("this", subAdapterDesc, null, l0, l7, 0);
		        mv.visitLocalVariable("clazz", "Ljava/lang/Class;", "Ljava/lang/Class<TT;>;", l0, l7, 1);
		        mv.visitLocalVariable("additionalDatas", "[Ljava/lang/Object;", null, l0, l7, 2);
		        mv.visitLocalVariable("webApplicationContext", "Lorg/springframework/context/ApplicationContext;", null, l1, l7, 3);
		        mv.visitLocalVariable("map", "Ljava/util/Map;", null, l2, l7, 4);
		        mv.visitLocalVariable("t", "Ljava/lang/Object;", "TT;", l3, l7, 5);
		        mv.visitLocalVariable("beanName", "Ljava/lang/String;", null, l6, l4, 6);
		        mv.visitMaxs(2, 7);
		        mv.visitEnd();
		        }
		        {
		        mv = cw.visitMethod(ACC_PUBLIC, "getAdditionalDatas", "(Ljava/lang/Class;)[Ljava/lang/Object;", "(Ljava/lang/Class<*>;)[Ljava/lang/Object;", null);
		        mv.visitCode();
		        Label l0 = new Label();
		        mv.visitLabel(l0);
		        mv.visitLineNumber(50, l0);
		        mv.visitLdcInsn(Type.getType("Lorg/springframework/context/ApplicationContext;"));
		        mv.visitMethodInsn(INVOKESTATIC, "com/alibaba/roma/web/util/ApplicationContextUtils", "get", "(Ljava/lang/Class;)Ljava/lang/Object;");
		        mv.visitTypeInsn(CHECKCAST, "org/springframework/context/ApplicationContext");
		        mv.visitVarInsn(ASTORE, 2);
		        Label l1 = new Label();
		        mv.visitLabel(l1);
		        mv.visitLineNumber(51, l1);
		        mv.visitVarInsn(ALOAD, 2);
		        mv.visitVarInsn(ALOAD, 1);
		        mv.visitMethodInsn(INVOKEINTERFACE, "org/springframework/context/ApplicationContext", "getBeansOfType", "(Ljava/lang/Class;)Ljava/util/Map;");
		        mv.visitVarInsn(ASTORE, 3);
		        Label l2 = new Label();
		        mv.visitLabel(l2);
		        mv.visitLineNumber(52, l2);
		        mv.visitVarInsn(ALOAD, 3);
		        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "keySet", "()Ljava/util/Set;");
		        mv.visitVarInsn(ASTORE, 4);
		        Label l3 = new Label();
		        mv.visitLabel(l3);
		        mv.visitLineNumber(54, l3);
		        mv.visitVarInsn(ALOAD, 4);
		        mv.visitVarInsn(ALOAD, 4);
		        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "size", "()I");
		        mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
		        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;");
		        mv.visitVarInsn(ASTORE, 5);
		        Label l4 = new Label();
		        mv.visitLabel(l4);
		        mv.visitLineNumber(55, l4);
		        mv.visitVarInsn(ALOAD, 5);
		        mv.visitInsn(ARETURN);
		        Label l5 = new Label();
		        mv.visitLabel(l5);
		        mv.visitLocalVariable("this", subAdapterDesc, null, l0, l5, 0);
		        mv.visitLocalVariable("clazz", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l0, l5, 1);
		        mv.visitLocalVariable("webApplicationContext", "Lorg/springframework/context/ApplicationContext;", null, l1, l5, 2);
		        mv.visitLocalVariable("map", "Ljava/util/Map;", null, l2, l5, 3);
		        mv.visitLocalVariable("keySet", "Ljava/util/Set;", null, l3, l5, 4);
		        mv.visitLocalVariable("beanNames", "[Ljava/lang/Object;", null, l4, l5, 5);
		        mv.visitMaxs(2, 6);
		        mv.visitEnd();
		        }
		        cw.visitEnd();

				subAdapterClass = adapterClassLoader.defineClass(subAdapterClassName, cw.toByteArray());
			}
		}
		try {
			adapterInstance = (RomaBeanAdapter) subAdapterClass.newInstance();
		} catch (InstantiationException e) {
		    throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
		    throw new RuntimeException(e);
		}
	}
	
	
	public Object getObject(Class<?> clazz, Object[] beanDatas) {

        return adapterInstance.getObject(clazz, beanDatas);
	}

	public Object[] getBeanDatas(Class<?> clazz) {

        return adapterInstance.getBeanDatas(clazz);
	}

}
