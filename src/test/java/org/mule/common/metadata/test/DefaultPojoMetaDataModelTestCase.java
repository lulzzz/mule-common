/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.common.metadata.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.mule.common.metadata.Capability;
import org.mule.common.metadata.DefaultPojoMetaDataModel;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.PojoMetaDataModel;
import org.mule.common.metadata.QueryCapability;
import org.mule.common.metadata.datatype.DataType;
import org.mule.common.metadata.test.pojo.EverythingPojo;

import java.util.List;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;

public class DefaultPojoMetaDataModelTestCase
{

    public interface A
    {

    }

    public interface B_A extends A
    {

    }

    public interface C_A extends A
    {

    }

    public interface D_BC extends B_A, C_A
    {

    }

    public static class ClassD implements D_BC
    {

    }

    public static class ClassA implements B_A
    {

    }

    public static class ClassE extends ClassA
    {

    }

    private static String nameA = A.class.getName();
    private static String nameB = B_A.class.getName();
    private static String nameC = C_A.class.getName();
    private static String nameD = D_BC.class.getName();
    private static String nameClassA = ClassA.class.getName();

    @Test
    public void testParentNames()
    {
        PojoMetaDataModel pojoModel = new DefaultPojoMetaDataModel(ClassD.class);
        assertExpectedParentNames(new String[] {"java.lang.Object", nameD, nameB, nameC, nameA}, pojoModel);

        pojoModel = new DefaultPojoMetaDataModel(ClassA.class);
        assertExpectedParentNames(new String[] {"java.lang.Object", nameB, nameA}, pojoModel);

        pojoModel = new DefaultPojoMetaDataModel(ClassE.class);
        assertExpectedParentNames(new String[] {"java.lang.Object", nameClassA, nameB, nameA}, pojoModel);

        pojoModel = new DefaultPojoMetaDataModel(Object.class);
        assertExpectedParentNames(new String[] {}, pojoModel);

        pojoModel = new DefaultPojoMetaDataModel(A.class);
        assertExpectedParentNames(new String[] {}, pojoModel);

        pojoModel = new DefaultPojoMetaDataModel(B_A.class);
        assertExpectedParentNames(new String[] {nameA}, pojoModel);

        pojoModel = new DefaultPojoMetaDataModel(D_BC.class);
        assertExpectedParentNames(new String[] {nameA, nameB, nameC}, pojoModel);

        pojoModel = new DefaultPojoMetaDataModel(Boolean.class);
        assertExpectedParentNames(new String[] {"java.lang.Object", "java.io.Serializable", "java.lang.Comparable"}, pojoModel);
    }

    @Test
    public void fullPojoTest()
    {
        DefaultPojoMetaDataModel defaultPojoMetaDataModel = new DefaultPojoMetaDataModel(EverythingPojo.class);
        assertThat(defaultPojoMetaDataModel.getFields().size(), CoreMatchers.is(17));
        for (MetaDataField metaDataField : defaultPojoMetaDataModel.getFields())
        {
            List<Capability> capabilities = metaDataField.getCapabilities();
            if (metaDataField.getMetaDataModel().getDataType() != DataType.POJO)
            {
                assertThat(capabilities.size(), CoreMatchers.is(1));
                assertThat(capabilities.get(0), CoreMatchers.is(QueryCapability.class));
                assertThat(((QueryCapability) capabilities.get(0)).isSelectCapable(), CoreMatchers.is(true));
                assertThat(((QueryCapability) capabilities.get(0)).isSortCapable(), CoreMatchers.is(true));
                assertThat(((QueryCapability) capabilities.get(0)).isWhereCapable(), CoreMatchers.is(true));
                assertThat("Operators should not be empty", ((QueryCapability) capabilities.get(0)).getSupportedOperators().isEmpty(), CoreMatchers.is(false));
            }
        }
    }

    private void assertExpectedParentNames(String[] expectedParentNames, PojoMetaDataModel pojoModel)
    {
        Set<String> actualParentNames = pojoModel.getParentNames();
        assertEquals("Not the expected number of parents. ExpectedParents=" + toString(expectedParentNames) + " ActualParents=" + actualParentNames, expectedParentNames.length, actualParentNames.size());
        for (String expectedParentName : expectedParentNames)
        {
            assertTrue("Missing parent: " + expectedParentName + " from ActualParents=" + actualParentNames, actualParentNames.contains(expectedParentName));
        }
    }

    private static String toString(String[] array)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++)
        {
            sb.append(array[i]);
            if (i + 1 < array.length)
            {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}


