package mypkg;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DeviceRule {
	private String rule_path = "rule.json";
	private JSONParser parser = new JSONParser();
	private JSONObject rule_obj = null;
	
	public DeviceRule() {}
	
	// read rule from .json file.
	public boolean init() {
		try {
			rule_obj = (JSONObject) parser.parse( new InputStreamReader(new FileInputStream(rule_path), "UTF-8"));
			System.out.println("[Read Rule Success]");
			return true;
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			System.out.println("[DeviceRule.java]: Read rule file failed ... " + e.getMessage());
			return false;
		}
	}
	
	// get device specific rules.
	public JSONObject getRule(String TypeId) {
		return (JSONObject) rule_obj.get(TypeId);
	}
}
