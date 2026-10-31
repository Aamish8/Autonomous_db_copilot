package com.aamish.db_copilot.service;
import java.util.*;
import java.util.regex.Pattern;

import net.sf.jsqlparser.statement.select.Select;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;

@Service
@RequiredArgsConstructor
public class SqlValidator {
	public void validate(String sql) throws JSQLParserException {
	    Statements statements = CCJSqlParserUtil.parseStatements(sql);
	    String text=statements.get(0).toString().toLowerCase();
	    final Pattern BANNED = Pattern.compile(
	            "\\b(pg_\\w*|dblink\\w*|lo_import|lo_export|copy|into|information_schema)\\b");
	    if(BANNED.matcher(text).find()) {
	    	throw new IllegalArgumentException("Query uses a blocked keyword or function");
	    }
	    if (statements.size() != 1) {
	        throw new IllegalArgumentException("Only one statement allowed");
	    }
	    if (!(statements.get(0) instanceof Select)) {
	        throw new IllegalArgumentException("Only SELECT is allowed");
	    }
	}
}
