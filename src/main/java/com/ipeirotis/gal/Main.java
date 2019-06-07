/*******************************************************************************
 * Copyright 2012 Panos Ipeirotis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ipeirotis.gal;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.ipeirotis.gal.engine.Engine;
import com.ipeirotis.gal.engine.EngineContext;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

public class Main {

	public static HashMap<Integer, Double> userId2Score = new HashMap<Integer, Double>();
	public static HashMap<Integer, HashMap<Integer, Double>> userId2TaskId2Score = new HashMap<Integer,
			HashMap<Integer, Double>>();

	/**
	 * Main Entry Point
	 * 
	 * @param args arguments
	 */
	public static void main(String[] args) {
		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setMaxCharsPerColumn(15000);
		TsvParser parserScore = new TsvParser(settings);
		List<String[]> usersScore = null;
		List<String[]> answerStatistics = null;
		try {
			usersScore = parserScore.parseAll(new BufferedReader(new FileReader("users_score.tsv")));
			answerStatistics = parserScore.parseAll(new BufferedReader(new FileReader("total_answer_statistics")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 1; i < usersScore.size(); i++) {
			String [] row = usersScore.get(i);
			userId2Score.put(Integer.parseInt(row[0]), Double.parseDouble(row[1]));
		}
		for (int i = 1; i < answerStatistics.size(); i++) {
			String [] row = answerStatistics.get(i);
			userId2TaskId2Score.computeIfAbsent(Integer.parseInt(row[1]), x -> new HashMap<>()).put(
					Integer.parseInt(row[0]), Double.parseDouble(row[3]));
		}
		EngineContext ctx = new EngineContext();
		
		CmdLineParser parser = new CmdLineParser(ctx);
		
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e);
			
			showUsage(parser);
			
			return;
		}

		Engine engine = new Engine(ctx);
		
		engine.execute();
	}

	private static void showUsage(CmdLineParser parser) {
		System.err.println("Usage: \n");
		parser.printUsage(System.err);
		System.err.println();
	}
}
