import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Tester {

	private static Scanner fin = null;
	private static PrintWriter pw = null;
	private static Property prop = null;
	private static ArrayList<Property> properties = new ArrayList<Property>();

	public static void main(String[] args) throws Exception {

		String fileName = "Student";
		String firstLetter = fileName.substring(0, 1).toLowerCase();

		fin = new Scanner(new File(fileName + ".txt"));
		pw = new PrintWriter(fileName + ".java");

		pw.println("import java.util.Scanner;");
		pw.println();
		pw.println("public class " + fileName + " {");

		//====================================================================== Write Properties
		pw.println("\n\t//=========================================================== Properties");

		String[] variables = fin.nextLine().split("\t");
		String[] data = fin.nextLine().split("\t");
		fin.close();
		
		for(int i = 0 ; i < data.length ; i++) {
			pw.println(declareProperty(data[i], variables[i]));
		}

		//====================================================================== Write Constructors
		pw.println("\n\t//=========================================================== Constructors");

		//---------------------------------------------------------------------- "Workhorse" constructor

		// Ex. public Car (int ID, String make, String model, ...) {
		pw.print("\tpublic " + fileName + " ");

		String tmp = "";
		for (Property p : properties) 
			tmp += ", " + p.writeTypeAndField();
		tmp = "(" + tmp.substring(2) + ") {";
		pw.println(tmp);

		// Ex.	setID(ID);
		//		setMake(make);
		//		setModel(model);
		//		}

		for (Property p : properties) {
			pw.println("\t\t"+ p.declareSetter());
		}
		pw.println("\t}\n");

		//---------------------------------------------------------------------- "Scanner" constructor

		//Ex.	public Car (Scanner fin) throws Exception {

		pw.println("\tpublic "+fileName+" (Scanner fin) throws Exception {");
		pw.println("\t\tString[] parts = fin.nextLine().split(\"\\t\");");

		for (int i = 0 ; i < properties.size() ; i++)
			printParsedSet(properties.get(i), i);

		pw.println("\t}");

		//====================================================================== Write Methods
		pw.println("\n\t//=========================================================== Methods");

		//---------------------------------------------------------------------- Equals method
		pw.println("\tpublic boolean equals (Object obj) {\n"
				+ "\t\tif(!(obj instanceof "+ fileName +")) return false;\n"
				+ "\t\t" + fileName + " " + firstLetter + " = ("+ fileName +") obj;\n" 
				+ "\t\treturn getEqualsString().equals(s.getEqualsString());\n" 
				+ "\t}");

		//---------------------------------------------------------------------- getEqualsString method
		pw.print("\n\tprivate String getEqualsString() {\n\t\treturn ");
		tmp = "";
		for (Property p : properties) 
			tmp += " + \"-\" + " + p.fieldName;
		pw.println(tmp.substring(9)+";\n\t}");

		//---------------------------------------------------------------------- toString method
		pw.print("\n\tpublic String toString() {\n\t\treturn ");
		tmp = "";
		for (Property p : properties)
			tmp += " + \", " + p.fieldName + ": \" + " + p.fieldName;
		pw.println("\"" + tmp.substring(6) + ";\n\t}");

		//====================================================================== Write Getters/Setters
		pw.println("\n\t//=========================================================== Getters/Setters");

		for (Property p : properties)
			pw.println(p.createGetter() + "\n");
		for (Property p : properties)
			pw.println(p.createSetter() + "\n");

		//===================
		pw.println("}");
		pw.close();
	} // end main

	private static void printParsedSet(Property p, int i) {

		switch (p.dataType) {
		case "int":
			pw.println("\t\tset"+p.uCase()+ 
					"(Integer.parseInt(parts["+i+"]));");
			break;
		case "double":
			pw.println("\t\tset"+p.uCase()+
					"(Double.parseDouble(parts["+i+"]));");
			break;
		case "boolean":
			pw.println("\t\tset"+p.uCase()+
					"(Boolean.parseBoolean(parts["+i+"]));");
			break;
		default:
			pw.println("\t\tset"+p.uCase()+
					"(parts["+i+"]);");
			break;
		}
	}

	private static String declareProperty(String data, String variable) {

		if (isInt(data))
			prop = new Property(variable, "int");
		else if (isLong(data))
			prop = new Property(variable, "long");
		else if (isFloat(data))
			prop = new Property(variable, "double");
		else if (isBoolean(data))
			prop = new Property(variable, "boolean");
		else
			prop = new Property(variable, "String");

		properties.add(prop);
		return "\t" + prop.createDeclaration();

	}

	// Methods for checking for datatype o
	public static boolean isInt(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (Exception e) {  return false;  }
	}

	public static boolean isLong(String val) {
		try {
			Long.parseLong(val);
			return true;
		} catch (Exception e) {  return false;  }
	}

	public static boolean isFloat(String val) {
		try {
			if(val.contains(".")) {
				Double.parseDouble(val);
				return true;
			}
			return false;
		} catch (Exception e) {  return false;  }

	}

	public static boolean isBoolean(String val) {
		return val.equalsIgnoreCase("True") || val.equalsIgnoreCase("False");		
	}

	//============================================
	//======== INNER CLASS "PROPERTY" ============
	//============================================

	public static class Property {

		//================================================= Properties
		public String fieldName;
		public String dataType;

		//================================================= Constructors
		public Property(String fieldName, String dataType) {
			super();
			setFieldName(fieldName);
			setDataType(dataType);
		}

		//================================================= Methods
		public String writeTypeAndField() {
			// String carMake
			return String.format("%s %s", dataType, lCase(fieldName));
		}

		public String createDeclaration() {
			// private String carMake
			return String.format("private %s %s;", dataType, fieldName);
		}

		public String createGetter() {
			//	public String getCarMake() { 
			//		return carMake; 
			//	}
			return String.format("\tpublic %s get%s() {\n"
					+ "\t\treturn this.%s;\n\t}", 
					dataType, uCase(fieldName), fieldName
					);
		}

		public String createSetter() {
			//	public void setCarMake(carMake) { 
			//		this.carMake = carMake; 
			//	}
			return String.format("\tpublic void set%s(%s %s) {\n" 
					+ "\t\tthis.%s = %s;\n\t}", 
					uCase(fieldName), dataType, lCase(fieldName), fieldName, lCase(fieldName)
					);
		}

		public String declareSetter() {
			//	setCarMake(carMake)
			return String.format("set%s(%s);", uCase(fieldName), lCase(fieldName));
		}

		private String uCase(String s) {
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}

		private String lCase(String s) {
			return s.substring(0, 1).toLowerCase()+s.substring(1);
		}

		public String uCase() {
			return uCase(this.fieldName);
		}

		public String lCase() {
			return lCase(this.fieldName);
		}
		//================================================= Getters/Setters

		public void setFieldName(String fieldName) 	{this.fieldName = fieldName;}
		public void setDataType(String dataType) 	{this.dataType = dataType;}

	} // End Property Class

} // End Tester Class


