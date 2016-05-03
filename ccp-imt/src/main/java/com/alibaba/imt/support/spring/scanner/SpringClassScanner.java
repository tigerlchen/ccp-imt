/**
 * Project: imt
 * 
 * File Created at 2012-9-17
 * $Id: SimpleClassScanner.java 17 2012-10-24 11:19:33Z yourxiaobai@gmail.com $
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.imt.support.spring.scanner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import com.alibaba.imt.scanner.Scanner;

/**
 * @author yansong.baiys
 *
 */
public class SpringClassScanner implements Scanner {

    private static final ResourcePatternResolver RESOLVER       = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory   READER_FACTORY = new SimpleMetadataReaderFactory();
    @Override
    public Set<String> scan(Set<String> paths){
        if(paths == null){
            throw new IllegalArgumentException("The paths argument cannot be null!");
        }
        Set<String> allClassResourceSet = new HashSet<String>();
        for(String path : paths){
            Set<String> classResourceSet = this.getClassInPath(path);
            allClassResourceSet.addAll(classResourceSet);
           
        }
        return allClassResourceSet;
    }
    
    private Set<String> getClassInPath(String path) {
        path = ClassUtils.convertClassNameToResourcePath(path);
        Set<String> ret = new HashSet<String>();
        Resource[] resources;
        try {
            resources = RESOLVER.getResources(path);
            for (Resource res : resources) {
                MetadataReader meta = READER_FACTORY.getMetadataReader(res);
                ret.add(ClassUtils.convertClassNameToResourcePath(meta.getClassMetadata().getClassName()) + ClassUtils.CLASS_FILE_SUFFIX);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

}
