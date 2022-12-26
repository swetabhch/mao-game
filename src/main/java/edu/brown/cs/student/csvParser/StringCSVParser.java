package edu.brown.cs.student.csvParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to parse CSVs structured in a way where their field names
 * are separated by commas on the first line of the file, with each
 * subsequent line containing the values of said fields separated by
 * corresponding commas.
 * Assumption: first field will be a unique identifier. Other field
 * values are stored as Strings in Lists.
 */
public class StringCSVParser {

  private String filename;
  private Map<String, List<String>> dataStringMap;
  private List<String> categories;

  /** A constructor that assigns a filename String to the CSVParser's internal
   * filename variable.
   * @param filename The filename String to set.
   */
  public StringCSVParser(String filename) {
    this.filename = filename;
    this.dataStringMap = new HashMap<>();
    this.categories = new ArrayList<>();
  }

  /** Parses a CSV using a BufferedReader in order to determine the categories of
   * data from the first line of the file and loads the rest of the data into a
   * Map. Throws an error if content of irregular structure is found or the file
   * isn't found.
   * This assumes that the first element of each line is a unique identifier.
   * @throws Exception When there is an error with finding or reading the file, or structuring
   * data obtained from the file.
   */
  public void loadCSVData() throws Exception {
    this.dataStringMap.clear();
    BufferedReader reader;
    try {
      FileReader fileReader = new FileReader(this.filename); //, StandardCharsets.UTF_8
      reader = new BufferedReader(fileReader);
    } catch (Exception e) {
      throw new Exception("ERROR: File not found.");
    }
    String categoryString;
    try {
      categoryString = reader.readLine();
      String[] categoryStrings = categoryString.split(",");
      Collections.addAll(this.categories, categoryStrings);
    } catch (Exception e) {
      throw new Exception("ERROR: Reading from empty file.");
    }

    try {
      String line = reader.readLine();
      while (line != null) {
        String[] components = line.split(",");
        if (components.length < 2) {
          this.dataStringMap.clear();
          throw new Exception("ERROR: Invalid CSV structure (< 2 fields).");
        }
        List<String> componentList = new ArrayList<>();
        for (int i = 1; i < components.length; i++) {
          componentList.add(components[i]);
        }
        if (line.charAt(line.length() - 1) == ',') {
          componentList.add("");
        }
        if ((componentList.size() + 1) != categories.size()) {
          throw new Exception("ERROR: Categories don't match entries in csv.");
        }
        this.dataStringMap.put(components[0], componentList);
        line = reader.readLine();
      }
    } catch (IOException e) {
      this.dataStringMap = new HashMap<>();
    }
    reader.close();
  }

  /** Sets the filename variable of the CSVParser to a specified String.
   * @param filename The filename String to set.
   */
  public void setFilename(String filename) {
    this.filename = filename;
  }

  /** Returns the current filename String associated with the CSVParser.
   * @return The filename String associated with the CSVParser.
   */
  public String getFilename() {
    return this.filename;
  }

  /** Returns a defensive copy of the Map of data loaded by the CSVParser.
   * @return A defensive copy of the data Map associated with the CSVParser.
   */
  public Map<String, List<String>> getDataStringMap() {
    return new HashMap<>(this.dataStringMap);
  }

  /** Returns a defensive copy of the List of categories loaded by the CSVParser.
   * @return A defensive copy of the category List associated with the CSVParser.
   */
  public List<String> getCategories() {
    return new ArrayList<>(this.categories);
  }
}
