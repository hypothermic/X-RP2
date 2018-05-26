/* X-RP - decompiled with CFR */
package eloraam.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class TagFile {

    TreeMap contents = new TreeMap();
    TreeMap comments = new TreeMap();
    String filecomment = "";

    public void addTag(String string, Object object) {
	int n = 0;
	TreeMap<String, Object> treeMap = this.contents;
	do {
	    String string2;
	    int n2;
	    if ((n2 = string.indexOf(46, n)) < 0) {
		string2 = string.substring(n);
		if (string2.equals("")) {
		    throw new IllegalArgumentException("Empty key name");
		}
		treeMap.put(string2, object);
		return;
	    }
	    string2 = string.substring(n, n2);
	    n = n2 + 1;
	    if (string2.equals("")) {
		throw new IllegalArgumentException("Empty key name");
	    }
	    Object v = treeMap.get(string2);
	    if (v == null) {
		TreeMap<String, Object> treeMap2 = new TreeMap<String, Object>();
		treeMap.put(string2, treeMap2);
		treeMap = treeMap2;
		continue;
	    }
	    if (!(v instanceof TreeMap)) {
		throw new IllegalArgumentException("Key not a dictionary");
	    }
	    treeMap = (TreeMap) v;
	} while (true);
    }

    public Object getTag(String string) {
	int n = 0;
	TreeMap treeMap = this.contents;
	do {
	    int n2;
	    String string2;
	    if ((n2 = string.indexOf(46, n)) < 0) {
		string2 = string.substring(n);
		return treeMap.get(string2);
	    }
	    string2 = string.substring(n, n2);
	    n = n2 + 1;
	    Object v = treeMap.get(string2);
	    if (!(v instanceof TreeMap)) {
		return null;
	    }
	    treeMap = (TreeMap) v;
	} while (true);
    }

    public Object removeTag(String string) {
	int n = 0;
	TreeMap treeMap = this.contents;
	do {
	    int n2;
	    String string2;
	    if ((n2 = string.indexOf(46, n)) < 0) {
		string2 = string.substring(n);
		return treeMap.remove(string2);
	    }
	    string2 = string.substring(n, n2);
	    n = n2 + 1;
	    Object v = treeMap.get(string2);
	    if (!(v instanceof TreeMap)) {
		return null;
	    }
	    treeMap = (TreeMap) v;
	} while (true);
    }

    public void commentTag(String string, String string2) {
	this.comments.put(string, string2);
    }

    public void commentFile(String string) {
	this.filecomment = string;
    }

    public void addString(String string, String string2) {
	this.addTag(string, string2);
    }

    public void addInt(String string, int n) {
	this.addTag(string, n);
    }

    public String getString(String string) {
	Object object = this.getTag(string);
	if (!(object instanceof String)) {
	    return null;
	}
	return (String) object;
    }

    public String getString(String string, String string2) {
	Object object = this.getTag(string);
	if (!(object instanceof String)) {
	    return string2;
	}
	return (String) object;
    }

    public int getInt(String string) {
	Object object = this.getTag(string);
	if (!(object instanceof Integer)) {
	    return 0;
	}
	return (Integer) object;
    }

    public int getInt(String string, int n) {
	Object object = this.getTag(string);
	if (!(object instanceof Integer)) {
	    return n;
	}
	return (Integer) object;
    }

    private void writecomment(PrintStream printStream, String string, String string2) {
	if (string2 == null) {
	    return;
	}
	for (String string3 : string2.split("\n")) {
	    printStream.printf("%s# %s\n", string, string3);
	}
    }

    private String collapsedtag(TreeMap treeMap, String string, String string2) {
	String string3 = string;
	Object v = treeMap.get(string);
	while (this.comments.get(string2) == null) {
	    if (v instanceof String) {
		return string3 + "=\"" + ((String) v).replace("\"", "\\\"") + "\"";
	    }
	    if (v instanceof Integer) {
		return string3 + "=" + (Integer) v;
	    }
	    treeMap = (TreeMap) v;
	    if (treeMap.size() != 1) {
		return null;
	    }
	    String string4 = (String) treeMap.firstKey();
	    string3 = string3 + "." + string4;
	    v = treeMap.get(string4);
	    string2 = string2 + "." + string4;
	}
	return null;
    }

    // X-RP: possible misdeclaration of treeMap in following method sig!!!
    private void savetag(PrintStream printStream, TreeMap<String, Integer> treeMap, String string, String string2) throws IOException {
	for (String string3 : treeMap.keySet()) {
	    String string4 = string == null ? string3 : string + "." + string3;
	    this.writecomment(printStream, string2, (String) this.comments.get(string4));
	    Object v = treeMap.get(string3);
	    if (v instanceof String) {
		printStream.printf("%s%s=\"%s\"\n", string2, string3, ((String) v).replace("\"", "\\\""));
		continue;
	    }
	    if (v instanceof Integer) {
		printStream.printf("%s%s=%d\n", string2, string3, (Integer) v);
		continue;
	    }
	    if (!(v instanceof TreeMap))
		continue;
	    String string5 = this.collapsedtag(treeMap, string3, string4);
	    if (string5 != null) {
		printStream.printf("%s%s\n", string2, string5);
		continue;
	    }
	    printStream.printf("%s%s {\n", string2, string3);
	    this.savetag(printStream, (TreeMap) v, string4, string2 + "    ");
	    printStream.printf("%s}\n\n", string2);
	}
    }

    public void saveFile(File file) {
	try {
	    FileOutputStream fileOutputStream = new FileOutputStream(file);
	    PrintStream printStream = new PrintStream(fileOutputStream);
	    this.writecomment(printStream, "", this.filecomment);
	    this.savetag(printStream, this.contents, null, "");
	    printStream.close();
	} catch (IOException iOException) {
	    iOException.printStackTrace();
	}
    }

    private static void readtag(TreeMap treeMap, StreamTokenizer streamTokenizer) throws IOException {
        block5 : while (streamTokenizer.nextToken() != -1 && streamTokenizer.ttype != 125) {
            if (streamTokenizer.ttype == 10) continue;
            if (streamTokenizer.ttype != -3) {
                throw new IllegalArgumentException("Parse error");
            }
            String string = streamTokenizer.sval;
            TreeMap treeMap2 = treeMap;
            Object object; // X-RP: Moved this from between switch and first case to here
            block6 : do {
                switch (streamTokenizer.nextToken()) {
                    case 46: {
                        Object v = treeMap2.get(string);
                        if (!(v instanceof TreeMap)) {
                            object = new TreeMap();
                            treeMap2.put(string, object);
                            treeMap2 = (TreeMap) object;
                        } else {
                            treeMap2 = (TreeMap)v;
                        }
                        streamTokenizer.nextToken();
                        if (streamTokenizer.ttype != -3) {
                            throw new IllegalArgumentException("Parse error");
                        }
                        string = streamTokenizer.sval;
                        continue block6;
                    }
                    case 61: {
                        streamTokenizer.nextToken();
                        if (streamTokenizer.ttype == -2) {
                            treeMap2.put(string, (int)streamTokenizer.nval);
                        } else if (streamTokenizer.ttype == 34) {
                            treeMap2.put(string, streamTokenizer.sval);
                        } else {
                            throw new IllegalArgumentException("Parse error");
                        }
                        streamTokenizer.nextToken();
                        if (streamTokenizer.ttype == 10) continue block5;
                        throw new IllegalArgumentException("Parse error");
                    }
                    case 123: {
                        object = treeMap2.get(string);
                        if (!(object instanceof TreeMap)) {
                            TreeMap treeMap3 = new TreeMap();
                            treeMap2.put(string, treeMap3);
                            treeMap2 = treeMap3;
                        } else {
                            treeMap2 = (TreeMap)object;
                        }
                        TagFile.readtag(treeMap2, streamTokenizer);
                        streamTokenizer.nextToken();
                        if (streamTokenizer.ttype == 10) continue block5;
                        throw new IllegalArgumentException("Parse error");
                    }
                }
                break;
            } while (true);
            throw new IllegalArgumentException("Parse error");
        }
    }

    public static TagFile loadFile(File file) {
	TagFile tagFile = new TagFile();
	try {
	    FileInputStream fileInputStream = new FileInputStream(file);
	    tagFile.readStream(fileInputStream);
	} catch (IOException iOException) {
	    iOException.printStackTrace();
	}
	return tagFile;
    }

    public void readFile(File file) {
	try {
	    FileInputStream fileInputStream = new FileInputStream(file);
	    this.readStream(fileInputStream);
	} catch (IOException iOException) {
	    iOException.printStackTrace();
	}
    }

    public void readStream(InputStream inputStream) {
	try {
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    StreamTokenizer streamTokenizer = new StreamTokenizer(bufferedReader);
	    streamTokenizer.commentChar(35);
	    streamTokenizer.eolIsSignificant(true);
	    streamTokenizer.lowerCaseMode(false);
	    streamTokenizer.parseNumbers();
	    streamTokenizer.quoteChar(34);
	    streamTokenizer.ordinaryChar(61);
	    streamTokenizer.ordinaryChar(123);
	    streamTokenizer.ordinaryChar(125);
	    streamTokenizer.ordinaryChar(46);
	    TagFile.readtag(this.contents, streamTokenizer);
	    inputStream.close();
	} catch (IOException iOException) {
	    iOException.printStackTrace();
	}
    }

    Query query(String string) {
	return new Query(string);
    }

    private static class QueryEntry {

	public TreeMap tag;
	public Iterator iter;
	public String path;
	int lvl;

	private QueryEntry() {
	}
    }

    public class Query implements Iterable {

	String[] pattern;

	public Iterator iterator() {
	    return new QueryIterator();
	}

	private Query(String string) {
	    this.pattern = string.split("\\.");
	}

	public class QueryIterator implements Iterator {

	    ArrayList path;
	    String lastentry;

	    private void step() {
		while (this.path != null) {
		    if (!this.step1())
			continue;
		    return;
		}
	    }

	    private boolean step1() {
		String string;
		QueryEntry queryEntry = (QueryEntry) this.path.get(this.path.size() - 1);
		if (!queryEntry.iter.hasNext()) {
		    this.path.remove(this.path.size() - 1);
		    if (this.path.size() == 0) {
			this.path = null;
		    }
		    return false;
		}
		String string2 = (String) queryEntry.iter.next();
		String string3 = string = queryEntry.path.equals("") ? string2 : queryEntry.path + "." + string2;
		if (queryEntry.lvl == Query.this.pattern.length - 1) {
		    this.lastentry = string;
		    return true;
		}
		Object v = queryEntry.tag.get(string2);
		if (!(v instanceof TreeMap)) {
		    return false;
		}
		return this.step0(queryEntry.lvl + 1, (TreeMap) v, string);
	    }

	    private boolean step0(int n, TreeMap treeMap, String string) {
		for (int i = n; i < Query.this.pattern.length; ++i) {
		    if (Query.this.pattern[i].equals("%")) {
			QueryEntry queryEntry = new QueryEntry();
			queryEntry.path = string;
			queryEntry.tag = treeMap;
			queryEntry.lvl = i;
			queryEntry.iter = treeMap.keySet().iterator();
			this.path.add(queryEntry);
			return false;
		    }
		    Object queryEntry = treeMap.get(Query.this.pattern[i]);
		    string = string.equals("") ? Query.this.pattern[i] : string + "." + Query.this.pattern[i];
		    if (!(queryEntry instanceof TreeMap)) {
			if (i != Query.this.pattern.length - 1)
			    break;
			this.lastentry = string;
			return true;
		    }
		    treeMap = (TreeMap) queryEntry;
		}
		this.path.remove(this.path.size() - 1);
		if (this.path.size() == 0) {
		    this.path = null;
		}
		return false;
	    }

	    @Override
	    public boolean hasNext() {
		return this.path != null;
	    }

	    public String next() {
		String string = this.lastentry;
		this.step();
		return string;
	    }

	    @Override
	    public void remove() {
	    }

	    private QueryIterator() {
		this.path = new ArrayList();
		TreeMap treeMap = TagFile.this.contents;
		Object var3_3 = null;
		if (!this.step0(0, TagFile.this.contents, "")) {
		    this.step();
		}
	    }
	}

    }

}
