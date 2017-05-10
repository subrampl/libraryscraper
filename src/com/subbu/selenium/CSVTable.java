package com.subbu.selenium;


import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CSVTable {
    private static final Logger LOG = LoggerFactory.getLogger(CSVTable.class);

    List<CSVTableRow> list = new ArrayList<CSVTableRow>();

    CSVWriter csvWriter = null;

    Writer writer = null;

    List<String> headerList;

    String csvFileName = "";

    int headerRow = 0;
    
    /**
     * Refers to the user who uploaded the CSV file. Used mainly to set the
     * createdBy field of the RawOffers that were part in the file.
     */
    private Integer createdBy = 0;

    public CSVTable(String csvFileName) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvFileName));
        init(reader);
    }

    public CSVTable(Reader sr) throws Exception {
        CSVReader reader = new CSVReader(sr);
        init(reader);
    }

   public CSVTable(String csvFileName, List<String> headerList) throws Exception {

        this.csvFileName = csvFileName;

        // Initiate writer
        File f = new File(csvFileName);
        // Setting UTF-8 encoding forcefully, files are saved in UTF-8 irrespective of platform encoding
        LOG.debug("Setting File Encoding to UTF-8");
        updateWriters(new PrintWriter(f,"UTF-8"));
        
        if (headerList != null) {
            csvWriter.writeNext(headerList.toArray(new String[0]));
        }
        this.headerList = headerList;
    }

    public void updateWriters(Writer writer) {
        this.writer = writer;
        csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER);
    }

    public CSVTableRow createRow() {
        CSVTableRow csvTableRow = new CSVTableRow(headerList);
        return csvTableRow;
    }

    public CSVTableRow createRow(String[] data) {
        CSVTableRow csvTableRow = new CSVTableRow(data, headerList);
        return csvTableRow;
    }

    public CSVTableRow createRow(List<String> data) {
        CSVTableRow csvTableRow = new CSVTableRow(data, headerList);
        return csvTableRow;
    }

    public void addRow(CSVTableRow csvTableRow) {
        list.add(csvTableRow);
    }

    public void save() throws Exception {
        for (CSVTableRow csvTableRow : list) {
            csvWriter.writeNext(csvTableRow.getData());
        }
        csvWriter.flush();
        csvWriter.close();

        writer.flush();
        writer.close();
    }

    public int getHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(int headerRow) {
        this.headerRow = headerRow;
    }

    private void init(CSVReader reader) throws Exception {
        List<String[]> listTransaction = reader.readAll();
        List<String> headerFieldList = new ArrayList(Arrays.asList(listTransaction.get(headerRow)));

        for (String[] strings : listTransaction.subList(1, listTransaction.size())) {
            // Ignore record if empty
            for (int i = 0; i < strings.length; i++) {
                if (StringUtils.isBlank(strings[i]) == false) {
                    // this field is not blank => line is not blank
                    // break out and go to next
                    list.add(new CSVTableRow(strings, headerFieldList));
                    break;
                }               
            }
        }
    }

    public List<CSVTableRow> getRecords() {
        return list;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    public CSVWriter getCsvWriter() {
        return csvWriter;
    }

    public Writer getWriter() {
        return writer;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
    
   /* public List<String> getHeaders() {
        List<String> headers = new ArrayList<String>();
        if (this.getRecords() == null || this.getRecords().size()==0) {
            return headers;
        }
        CSVTableRow firstDataRow = this.getRecords().get(0);
        headers.addAll(firstDataRow.headerFieldMap.keySet());
        return headers;
    }
*/
    public List<String> getOrderedHeaders() {
        List<String> headers = new ArrayList<String>();
        TreeMap<Integer,String> tempMap = new TreeMap<Integer, String>();
        if (this.getRecords() == null || this.getRecords().size()==0) {
            return headers;
        }
        CSVTableRow firstDataRow = this.getRecords().get(0);
        
        for(Entry<String, Integer> entry : firstDataRow.headerFieldMap.entrySet())
            tempMap.put(entry.getValue(),entry.getKey());
        
        for(int i=0;i<tempMap.size();i++)
            headers.add(tempMap.get(i).toString());
        
        return headers;
    }
    
    public List<CSVTableRow> getRecordsWithValue(String column, String value) {
        List<CSVTableRow> refinedList = new ArrayList<>();
        for(CSVTableRow table:list){
            if(StringUtils.isNotBlank(value) && StringUtils.isNotBlank(column) &&  value.equalsIgnoreCase(table.getString(column))){
                refinedList.add(table);
            }
        }
        return refinedList;
    }


}
