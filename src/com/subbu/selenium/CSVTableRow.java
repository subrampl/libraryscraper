package com.subbu.selenium;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVTableRow implements Serializable{
    
    public static final String BLANK = "";

    Map<String, Integer> headerFieldMap = new TreeMap<String, Integer>();

    Map<Integer, String> dataFieldMap = new TreeMap<Integer, String>();
    
    List<String> headerList = new ArrayList<String>();

    List<String> dataArray = null;

    public CSVTableRow() {

    }
    
    public CSVTableRow(CSVTableRow csvTableRow){
        this.headerFieldMap = new TreeMap<String, Integer>(csvTableRow.getHeaderFieldMap());
        this.dataFieldMap = new TreeMap<Integer, String>(csvTableRow.getDataFieldMap());
        this.headerList = new ArrayList<String>(csvTableRow.getHeaderList());
        this.dataArray = new ArrayList<String>(csvTableRow.getDataArray());
    }

    public CSVTableRow(List<String> headerField) {
        for (int j = 0; j < headerField.size(); j++) {
            headerFieldMap.put(headerField.get(j), j);
        }
        dataArray = new ArrayList<String>(headerField.size());
        for (int i = 0; i < headerField.size(); i++) {
            dataArray.add(null);
        }
        headerList.clear();
        headerList.addAll(headerField);
    }

    public CSVTableRow(String[] data, List<String> headerField) {
        for (int j = 0; j < headerField.size(); j++) {
            headerFieldMap.put(headerField.get(j), j);
        }
        this.dataArray = new ArrayList(Arrays.asList(data));
    }

    CSVTableRow(List<String> data, List<String> headerField) {
        for (int j = 0; j < headerField.size(); j++) {
            headerFieldMap.put(headerField.get(j), j);
        }
        this.dataArray = data;
    }

    public void setCell(String name, String value1, boolean createNew) {
        String value = value1;
        if (headerFieldMap.get(name) == null && !createNew) { return; }

        Integer position = headerFieldMap.get(name);
        value = value == null ? BLANK : value;

        value = value.replaceAll("\"", "'");

        if (position == null && createNew) {
            position = headerFieldMap.size();
            headerFieldMap.put(name, position);
            if (dataArray == null) {
                dataArray = new ArrayList<String>();
                dataArray.add(position, value);
            } else {
                dataArray.add(position, value);
            }
        } else {
            dataArray.set(position, value);
        }

    }

    public void setCell(String name, String value) {
        setCell(name, value, false);
    }

    public String[] getData() {
        return dataArray.toArray(new String[0]);
    }

    private static final Logger LOG = LoggerFactory.getLogger(CSVTableRow.class);

    /** String values which would evaluate to Boolean.TRUE */
    private static String[] trueValues = { "TRUE", "T", "YES", "Y", "1", "ON" };

    /** String values which would evaluate to Boolean.FALSE */
    private static String[] falseValues = { "FALSE", "F", "NO", "N", "0", "OFF" };

    /** The locale to use when converting dates, floats and decimals */
    private Locale locale = Locale.getDefault();

    /** The DateFormat to use for converting dates */
    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);

    /** The NumberFormat to use when converting floats and decimals */
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

    public String getValue(String name) {
        String value = "";
        if(name == null)
            return null;
        if (headerFieldMap.get(name) == null) { return value; }

        int position = headerFieldMap.get(name);
        value = dataArray.get(position);
        return value;
    }

    public String getString(String name) {
        String value = getValue(name);
        return getVString(value, null);
    }

    /**
     * Set the date format that will be used by this ValueParser.
     */
    public void setDateFormat(DateFormat df) {
        dateFormat = df;
    }

    /**
     * Get the date format that will be used by this ValueParser.
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Set the number format that will be used by this ValueParser.
     */
    public void setNumberFormat(NumberFormat nf) {
        numberFormat = nf;
    }

    /**
     * Get the number format that will be used by this ValueParser.
     */
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    /**
     * Returns a Boolean object for the given string. If the value can not be
     * parsed as a boolean, null is returned.
     * <p>
     * Valid values for true: true, t, on, 1, yes, y<br>
     * Valid values for false: false, f, off, 0, no, n<br>
     * <p>
     * The string is compared without reguard to case.
     * 
     * @param string
     *            A String with the value.
     * @return A Boolean.
     */
    private Boolean parseBoolean(String string) {
        Boolean result = null;
        String value = StringUtils.trim(string);

        if (!isNullString(value)) {
            for (int cnt = 0; cnt < Math.max(trueValues.length, falseValues.length); cnt++) {
                // Short-cut evaluation or bust!
                if ((cnt < trueValues.length) && value.equalsIgnoreCase(trueValues[cnt])) {
                    result = Boolean.TRUE;
                    break;
                }

                if ((cnt < falseValues.length) && value.equalsIgnoreCase(falseValues[cnt])) {
                    result = Boolean.FALSE;
                    break;
                }
            }

            if (result == null) {
                if (LOG.isWarnEnabled()) {
                    LOG.error("Parameter with value of (" + value + ") could not be converted to a Boolean");
                }
            }
        }

        return result;
    }

    private boolean isNullString(String value) {
        return StringUtils.isBlank(value) || StringUtils.equalsIgnoreCase(value, "null") || StringUtils.equalsIgnoreCase(value, "na") || StringUtils.equalsIgnoreCase(value, "n/a");
    }

    /**
     * Return a boolean for the given name. If the name does not exist, return
     * defaultValue.
     * 
     * @param name
     *            A String with the name.
     * @param defaultValue
     *            The default value.
     * @return A boolean.
     */
    public boolean getBoolean(String name, boolean defaultValue) {
        Boolean result = parseBoolean(name);
        return (result == null ? defaultValue : result.booleanValue());
    }

    /**
     * Return a boolean for the given name. If the name does not exist, return
     * false.
     * 
     * @param name
     *            A String with the name.
     * @return A boolean.
     */
    public boolean getBoolean(String name) {
        String value = getValue(name);
        return getBoolean(value, false);
    }

    /**
     * Return a {@link Number} for the given string.
     * 
     * @param string
     *            A String with the value.
     * @return A Number.
     * 
     */
    public Number parseNumber(String string) {
        Number result = null;
        String value = StringUtils.trim(string);

        if (!isNullString(value)) {
            ParsePosition pos = new ParsePosition(0);
            Number number = numberFormat.parse(value, pos);

            if (pos.getIndex() == value.length()) {
                // completely parsed
                result = number;
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Parameter with value of (" + value + ") could not be converted to a Number at position " + pos.getIndex());
                }
            }
        }

        return result;
    }

    /**
     * Return a {@link Number} for the given name. If the name does not exist,
     * return null. This is the base function for all numbers.
     * 
     * @param name
     *            A String with the name.
     * @return A Number.
     * 
     */
    private Number getNumber(String value) {
        return parseNumber(value);
    }

    /**
     * Return a double for the given name. If the name does not exist, return
     * defaultValue.
     * 
     * @param name
     *            A String with the name.
     * @param defaultValue
     *            The default value.
     * @return A double.
     */
    public double getDouble(String name, double defaultValue) {
        Number number = getNumber(name);
        return (number == null ? defaultValue : number.doubleValue());
    }

    /**
     * Return a double for the given name. If the name does not exist, return
     * 0.0.
     * 
     * @param name
     *            A String with the name.
     * @return A double.
     */
    public double getDouble(String name) {
        String value = getValue(name);
        return getDouble(value, 0.0);
    }

    /**
     * Return a float for the given name. If the name does not exist, return
     * defaultValue.
     * 
     * @param name
     *            A String with the name.
     * @param defaultValue
     *            The default value.
     * @return A float.
     */
    public float getFloat(String name, float defaultValue) {
        String value = getValue(name);
        Number number = getNumber(value);
        return (number == null ? defaultValue : number.floatValue());
    }

    /**
     * Return a float for the given name. If the name does not exist, return
     * 0.0.
     * 
     * @param name
     *            A String with the name.
     * @return A float.
     */
    public float getFloat(String name) {
        return getFloat(name, 0.0f);
    }


    /**
     * Return a BigDecimal for the given name. If the name does not exist,
     * return defaultValue.
     * 
     * @param name
     *            A String with the name.
     * @param defaultValue
     *            The default value.
     * @return A BigDecimal.
     */
    public BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {
        Number result = getNumber(name);
        return (result == null ? defaultValue : new BigDecimal(result.doubleValue()));
    }

    /**
     * Return a BigDecimal for the given name. If the name does not exist,
     * return null.
     * 
     * @param name
     *            A String with the name.
     * @return A BigDecimal.
     */
    public BigDecimal getBigDecimal(String name) {
        String value = getValue(name);
        return getBigDecimal(value, null);
    }

    /**
     * Return an int for the given name. If the name does not exist, return
     * defaultValue.
     * 
     * @param value
     *            A String with the name.
     * @param defaultValue
     *            The default value.
     * @return An int.
     */
    public int getInt(String value, int defaultValue) {
        Number result = getNumber(value);
        return ((result == null || result instanceof Double) ? defaultValue : result.intValue());
    }

    /**
     * Return an int for the given name. If the name does not exist, return 0.
     * 
     * @param name
     *            A String with the name.
     * @return An int.
     */
    public int getInt(String name) {
        String value = getValue(name);
        return getInt(value, 0);
    }

    public Integer getInteger(String name) {
        return getInteger(name, null);
    }

    public Integer getInteger(String name, Integer defaultValue) {
        Integer ret = null;
        String value = getValue(name);
        Number result = getNumber(value);
        try {
            ret = (result != null) ? ((Integer) result.intValue()) : defaultValue;
        } catch (Exception e) {
            LOG.error(e.toString());
        }
        return ret;
    }

    /**
     * Return a long for the given name. If the name does not exist, return
     * defaultValue.
     * 
     * @param name
     *            A String with the name.
     * @param defaultValue
     *            The default value.
     * @return A long.
     */
    public Long getLong(String name, Long defaultValue) {
        Long ret = null;
        String value = getValue(name);
        Number result = getNumber(value);
        try {
            ret = (result != null) ? ((Long) result.longValue()) : defaultValue;
        } catch (Exception e) {
            LOG.error(e.toString());
        }
        return ret;
    }

    /**
     * Return a long for the given name. If the name does not exist, return 0.
     * 
     * @param name
     *            A String with the name.
     * @return A long.
     */
    public long getLong(String name) {
        return getLong(name, null);
    }

    /**
     * Return a String for the given name. If the name does not exist, return
     * the defaultValue.
     * 
     * @param name
     *            A String with the name.
     * @param defaultValue
     *            The default value.
     * @return A String.
     */
    public String getString(String name, String defaultValue) {
        String value = getValue(name);
        return getVString(value, defaultValue);
    }

    private String getVString(String name, String defaultValue) {
        return (isNullString(name) ? defaultValue : name.trim());
    }

    /**
     * Returns a {@link java.util.Date} object. String is parsed by supplied
     * DateFormat. If the name does not exist or the value could not be parsed
     * into a date return the defaultValue.
     * 
     * @param name
     *            A String with the name.
     * @param df
     *            A DateFormat.
     * @param defaultValue
     *            The default value.
     * @return A Date.
     */
    public Date getDate(String name, DateFormat df, Date defaultValue) {
        Date result = defaultValue;
        String value = StringUtils.trim(name);

        if (StringUtils.isNotEmpty(value)) {
            try {
                // Reject invalid dates.
                df.setLenient(false);
                result = df.parse(value);
            } catch (ParseException e) {
                // log conversion error
            }
        }

        return result;
    }

    /**
     * Returns a {@link java.util.Date} object. If there are DateSelector or
     * TimeSelector style parameters then these are used. If not and there is a
     * parameter 'name' then this is parsed by DateFormat. If the name does not
     * exist, return null.
     * 
     * @param name
     *            A String with the name.
     * @return A Date.
     */
    public Date getDate(String name) {
        String value = getValue(name);
        return getDate(value, dateFormat, null);
    }

    /**
     * Returns a {@link java.util.Date} object. String is parsed by supplied
     * DateFormat. If the name does not exist, return null.
     * 
     * @param name
     *            A String with the name.
     * @param df
     *            A DateFormat.
     * @return A Date.
     */
    public Date getDate(String name, DateFormat df) {
        return getDate(name, df, null);
    }

    public List<String> getList(String name) {
        String value = getValue(name);
        List<String> list = new ArrayList<String>();
        if (!isNullString(value)) {
            list = Arrays.asList(value.split(","));
        }
        return list;
    }

    public List<String> getList(String name, String delim) {
        String value = getValue(name);
        List<String> list = new ArrayList<String>();
        if (!isNullString(value)) {
            list = Arrays.asList(value.split(delim));
        }
        return list;
    }

    public String getHashValue() {
        String resultValue = null;
        for (String value : this.dataArray) {
            if (resultValue == null) {
                resultValue = value;
            } else {
                resultValue = resultValue + "," + value;
            }
        }

        return UUID.nameUUIDFromBytes(StringUtils.trim(resultValue).getBytes()).toString();
    }

    public List<String> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<String> headerList) {
        this.headerList = headerList;
    }
    public boolean isColumnPresent(String columnName){
        return this.headerFieldMap.get(columnName) != null;
    }

    public Map<String, Integer> getHeaderFieldMap() {
        return headerFieldMap;
    }

    public Map<Integer, String> getDataFieldMap() {
        return dataFieldMap;
    }

    public List<String> getDataArray() {
        return dataArray;
    }



}
