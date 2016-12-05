/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.streaming.cql.executor.executorplan;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.BeforeClass;
import org.junit.Test;

import com.huawei.streaming.api.PhysicalPlan;
import com.huawei.streaming.application.Application;
import com.huawei.streaming.cql.executor.ExecutorPlanGenerator;
import com.huawei.streaming.cql.executor.PhysicalPlanLoader;
import com.huawei.streaming.cql.executor.PhysicalPlanLoaderTest;
import com.huawei.streaming.operator.IRichOperator;
import com.huawei.streaming.operator.functionstream.JoinFunctionOp;

/**
 * 执行计划生成器测试
 *
 */
public class JoinTest
{
    private static final String BASICPATH = File.separator + "executor" + File.separator + "plans" + File.separator;
    
    private static String serdeDir = null;
    
    /**
     * 类初始化之前要执行的方法
     *
     */
    @BeforeClass
    public static void setUpBeforeClass()
        throws Exception
    {
        /*
         * 初始化反序列化文件路径
         */
        setDir();
    }
    
    /**
     * 简单的filter执行用例测试
     *
     */
    @Test
    public void testJoin()
        throws Exception
    {
        
        String deSerFileName = "Join.xml";
        PhysicalPlan plan = PhysicalPlanLoader.load(serdeDir + deSerFileName);
        
        Application app = new ExecutorPlanGenerator().generate(plan.getApploication());
        assertTrue(app.getAppName().equals("join"));
        
        IRichOperator agginfo = null;
        for (IRichOperator finfo : app.getFunctionstreams())
        {
            if (finfo.getOperatorId().equals("join"))
            {
                agginfo = finfo;
            }
        }
        assertTrue(agginfo.getParallelNumber() == 1);
        assertTrue(agginfo instanceof JoinFunctionOp);
        
    }
    
    /**
     * 设置待序列化的文件路径
     *
     */
    private static void setDir()
    {
        String classPath = PhysicalPlanLoaderTest.class.getResource("/").getPath();
        
        try
        {
            classPath = URLDecoder.decode(classPath, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        
        serdeDir = classPath + BASICPATH + File.separator;
    }
}
