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

package com.huawei.streaming.serde;

import com.huawei.streaming.config.StreamingConfig;
import com.huawei.streaming.util.datatype.TimestampParser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.huawei.streaming.event.Attribute;
import com.huawei.streaming.event.TupleEventType;
import com.huawei.streaming.exception.StreamSerDeException;
import com.huawei.streaming.exception.StreamingException;
import com.huawei.streaming.support.SupportConst;

/**
 * 
 * <SimpleSerdeTest>
 * <SimpleSerdeTest>
 * 
 */
public class SimpleSerdeTest
{
    private static final int I_68 = 68;
    
    private static TupleEventType schema;
    private static TupleEventType schema2;
    private static TupleEventType schema3;
    private static TupleEventType schema4;

    static
    {
        
        List<Attribute> atts = Lists.newArrayList();
        atts.add(new Attribute(String.class, "a"));
        atts.add(new Attribute(String.class, "b"));
        atts.add(new Attribute(String.class, "c"));
        atts.add(new Attribute(String.class, "d"));
        schema = new TupleEventType("S1", atts);

        List<Attribute> atts2 = Lists.newArrayList();
        atts2.add(new Attribute(Timestamp.class, "a"));
        schema2 = new TupleEventType("S2", atts2);

        List<Attribute> atts3 = Lists.newArrayList();
        atts3.add(new Attribute(Timestamp.class, "a"));
        atts3.add(new Attribute(String.class, "b"));
        schema3 = new TupleEventType("S3", atts3);


        List<Attribute> atts4 = Lists.newArrayList();
        atts4.add(new Attribute(Boolean.class, "a"));
        schema4 = new TupleEventType("S4", atts4);
    }
    
    /**
     * <testDeserialize1>
     * <功能详细描述>
     */
    @Ignore
    @Test
    public void testDeserialize1()
     throws StreamSerDeException, IOException, StreamingException
    {
        StreamingConfig config = new StreamingConfig();
        SimpleSerDe deser = new SimpleSerDe();
        deser.setConfig(config);
        deser.setSchema(schema);
        deser.initialize();
        String s =
            "0,USER4,1379927089198,,USER4,RETL0,TERM_RETL0,2,RETL0,,CN,GZ,XA,02,"
                + "29539.825183043013,29539.825183043013,CNY,4923.304197173836,"
                + "2759854263445,null,null,false,,6,10,002,0210,000,,,903,,,,,,,,,,,,,,"
                + "710076,,00,20164.96066506037,0,,,,,,,0,0,,,,null,0,,0.0,,,,";
        int len = deser.deSerialize(s).get(0).length;
        assertTrue(len == I_68);
    }
    
    /**
     * <testDeserialize2>
     * <功能详细描述>
     */
    @Test
    public void testDeserialize2()
     throws StreamSerDeException, IOException, StreamingException
    {
        StreamingConfig config = new StreamingConfig();
        SimpleSerDe deser = new SimpleSerDe();
        deser.setConfig(config);
        deser.setSchema(schema);
        deser.initialize();
        String s = ",,,";
        int len = deser.deSerialize(s).get(0).length;
        assertTrue(len == SupportConst.I_FOUR);
    }
    
    /**
     * <testDeserialize3>
     * <功能详细描述>
     */
    @Test
    public void testDeserialize3()
     throws StreamSerDeException, IOException, StreamingException
    {
        StreamingConfig config = new StreamingConfig();
        SimpleSerDe deser = new SimpleSerDe();
        deser.setConfig(config);
        deser.setSchema(schema);
        deser.initialize();
        String s = "a,b,c,";
        int len = deser.deSerialize(s).get(0).length;
        assertTrue(len == SupportConst.I_FOUR);
    }

    /**
     * <testDeserialize3>
     * <功能详细描述>
     */
    @Test
    public void testDeserialize4()
     throws StreamSerDeException, IOException, StreamingException
    {
        StreamingConfig config = new StreamingConfig();
        SimpleSerDe deser = new SimpleSerDe();
        deser.setConfig(config);
        deser.setSchema(schema);
        deser.initialize();
        String s = "";
        assertTrue(deser.deSerialize(s).size() == SupportConst.I_ZERO);
    }

    /**
     * <testDeserialize3>
     * <功能详细描述>
     */
    @Test
    public void testDeserialize5()
     throws StreamSerDeException, IOException, StreamingException
    {
        StreamingConfig config = new StreamingConfig();
        SimpleSerDe deser = new SimpleSerDe();
        deser.setConfig(config);
        deser.setSchema(schema4);
        deser.initialize();
        assertTrue((Boolean)deser.deSerialize("true").get(0)[0]);
        assertTrue((Boolean)deser.deSerialize("TRUE").get(0)[0]);
        assertTrue((Boolean)deser.deSerialize("tRue").get(0)[0]);

        assertFalse((Boolean)deser.deSerialize("false").get(0)[0]);
        assertFalse((Boolean)deser.deSerialize("FALSE").get(0)[0]);
        assertFalse((Boolean)deser.deSerialize("fAlse").get(0)[0]);

        assertNull(deser.deSerialize("a").get(0)[0]);
        assertNull(deser.deSerialize(" ").get(0)[0]);
    }

    /**
     * <testSerialize>
     * <功能详细描述>
     */
    @Test
    public void testSerialize()
        throws StreamSerDeException, IOException, StreamingException
    {
        StreamingConfig config = new StreamingConfig();
        Object[] obj = {new TimestampParser(config).createValue("2014-10-05 11:08:00")};
        List<Object[]> events = new ArrayList<Object[]>();
        events.add(obj);

        SimpleSerDe deser = new SimpleSerDe();
        deser.setConfig(config);
        deser.setSchema(schema2);
        deser.initialize();

        Object result = deser.serialize(events);
        assertTrue("2014-10-05 11:08:00.000 +0800".equals(result.toString()));
    }

    /**
     * <testSerialize>
     * <功能详细描述>
     */
    @Test
    public void testSerialize2()
     throws StreamSerDeException, IOException, StreamingException
    {
        SimpleSerDe serde =  new SimpleSerDe();
        StreamingConfig config = new StreamingConfig();
        config.put(StreamingConfig.SERDE_SIMPLESERDE_SEPARATOR,"---");
        serde.setConfig(config);
        serde.setSchema(schema3);
        serde.initialize();
        Object[] obj = {new TimestampParser(config).createValue("2014-10-05 11:08:00"),"a"};
        Object[] obj2 = {new TimestampParser(config).createValue("2014-10-05 11:08:00"),"b"};
        List<Object[]> events = new ArrayList<Object[]>();
        events.add(obj);
        events.add(obj2);
        Object result = serde.serialize(events);
        assertTrue("2014-10-05 11:08:00.000 +0800---a\n2014-10-05 11:08:00.000 +0800---b".equals(result.toString()));
    }
}
