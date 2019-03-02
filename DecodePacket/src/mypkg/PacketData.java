package mypkg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PacketData {
	private static JSONArray allData = new JSONArray();
	final int TYPEIDINDEX = 9;
	private String timestamp;	// timesatmp when packet received.	[HH:MM:SS]
	
//	private String[] arrRule;
	private String[] arrHexData;		// hex packet data in string array
	private String TypeId;				// device type id [ex. 132, 135, 138, 152]
	private JSONObject packetRule;		// rule for this packet id
	private JSONObject decodeResult;	// decode result in json
	
	
	public PacketData() {}
	public PacketData(String timestamp, String strHexData) {
		this.timestamp = timestamp;
		
		// split data into array format to decode.
		arrHexData = strHexData.split(" ");
		
		// get the device type id in Decimal_string [ex. 132, 135, 138, 152]
		TypeId = Integer.parseInt(arrHexData[TYPEIDINDEX], 16) + "";
	}
	
	public void decode() {
		// could not match the type id in .json file, so can not decoed.
		if(packetRule == null) {
			System.out.println("[ERROR Type ID] >> Colud not decoding ...");
		}else {
			decodeResult = getObjectValue(packetRule);

			allData.add(decodeResult);	// add result to this JSONArray.
			System.out.print("[Done]\n");
		}
	}
	
	// get and store all Key:Value pairs, then return the final JSONObject
	public JSONObject getObjectValue(JSONObject obj) {
		JSONObject result = new JSONObject();
		
		Set keys = obj.keySet();
		Iterator iterator = keys.iterator();
		
		while(iterator.hasNext()) {
	    	String key = (String) iterator.next();
	        // Value equals JSONObject > recursively to get object value 
	        if(obj.get(key).getClass().equals(JSONObject.class)) {
	        	result.put(key, getObjectValue((JSONObject)obj.get(key)));
	        }else {
	        	// [key point] if key=value, we need to decode from hex byte code data
	        	if(key.equals("value")) {
	        		double res = getRuleResult(obj.get(key) + "");
	        		result.put(key, res);
	        	}else
	        		result.put(key, obj.get(key));
	        }
	    }
		return result;
	}
	
	public double getRuleResult(String rule) {
		String[] arrRule = rule.split(" ");
		double result = 0;
		
		// analyze the value rule ex.[i 13 - i 14 * v 100]
		Queue<String> que = processQueue(arrRule);
		
		// calculate the value from the queue.
		while(!que.isEmpty()) {
			switch((String) que.peek()) {
			case "*":
				que.poll();
				result *= Integer.parseInt((String) que.poll());
				break;
			case "-":
				que.poll();
				result -= Integer.parseInt((String) que.poll());
				break;
			case "/":
				que.poll();
				result /= Integer.parseInt((String) que.poll());
				break;
			case "+":
				que.poll();
				result += Integer.parseInt((String) que.poll());
				break;
			default:
				result = Integer.parseInt((String) que.poll());
				break;
			}
		}
		return result;
	}
	
	// analyze the value rule ex.[i 13 - i 14 * v 100]
	public Queue<String> processQueue(String[] arrRule){
		Queue<String> que = new LinkedList<String>();
		
		for(int i = 0 ; i < arrRule.length ; i++) {
			switch(arrRule[i]) {
			case "*":
			case "-":
			case "/":
			case "+":
				que.add(arrRule[i]);
				break;
			case "i":	// [index]: get value from hexData
				int index = Integer.parseInt(arrRule[++i])-1;
				que.add(Integer.parseInt(arrHexData[index], 16) + "");
				break;
			case "v":	// [value]: constant
				que.add(arrRule[++i]);
				break;
			default:
				System.out.println("[UNKNOW rule representation] > " + arrRule[i]);
				break;				
			}
		}
		return que;
	}
	
	// CRC = 55 XOR byte2 to byte18 must match byte49.
	public boolean checkCRC() {
		int CRC = 55;
		
		// CRC=55 XOR( byte2~byte18 )
		for(int i = 1 ; i < 18 ; i++) {
			CRC = CRC ^ Integer.parseInt(arrHexData[i], 16);
		}
		
		if(Integer.parseInt(arrHexData[48], 16) == CRC)
			return true;
		else
			return false;
	}
	
	// getter and setter
	public String getTypeId() {
		return this.TypeId;
	}
	
	public static JSONArray getAllData() {
		return allData;
	}
	
	public JSONObject getDecodeResult() {
		return decodeResult;
	}
	
	public void setRule(JSONObject rule) {
		this.packetRule = rule;
	}
}
