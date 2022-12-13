package hexi.nlp;

import java.util.Map;

import org.neo4j.graphdb.Node;

public class NodeEvaluator implements IisMatchNode{
	public String getProp_key() {
		return mProp_key;
	}

	public void setProp_key(String prop_key) {
		mProp_key = prop_key;
	}

	public String getProp_value() {
		return mProp_value;
	}

	public void setProp_value(String prop_value) {
		mProp_value = prop_value;
	}

	public Map<String, Object> getProperties() {
		return mProperties;
	}

	public void setProperties(Map<String, Object> properties) {
		mProperties = properties;
	}

	String mProp_key, mProp_value;
	Map<String,Object> mProperties;
	
	public boolean match(Node node) {
		if (node.getProperty(mProp_key).equals(mProp_value)) {
			return true;
		}else {
			return false;
		}
	}

}
