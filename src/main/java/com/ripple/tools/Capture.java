package com.ripple.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.ripple.tools.objects.LedgerRequest;
import com.ripple.tools.objects.LedgerRequestParam;
import com.ripple.tools.objects.LedgerResponse;
import com.ripple.tools.objects.LedgerStats;
import com.ripple.tools.objects.Record;
import com.ripple.tools.objects.ServerInfoRequest;
import com.ripple.tools.objects.ServerInfoResponse;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Capture {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS z");
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static void main(String[] args) throws Exception {

		if (args.length < 4) {
			System.out.println("Bad parameter");
			System.exit(1);
		}

		String url = args[0];
		String path = args[1];
		int interval = Integer.valueOf(args[2]);
		int count = Integer.valueOf(args[3]);

		String id = "capture-" + new Date().getTime();
		String capturePath = path + "/" + id;
		Files.createDirectories(Paths.get(capturePath));

		System.out.println("[INFO] Starting capture");
		System.out.println("- Server: " + url);
		System.out.println("- Id: " + id);
		System.out.println("- Path: " + capturePath);
		System.out.println("- Count: " + count);
		System.out.println("- Interval: " + interval + "(s)");

		try {
			OkHttpClient httpClient = new OkHttpClient();
			List<Record> records = new ArrayList<>();
			Long start = null;
			Integer firstIndex = null;
			Integer lastIndex = null;
			for (int i = 0; i < count; i++) {
				System.out.println("[INFO] Requesting server info (" + (i + 1) + "/" + count + ") ...");
				ServerInfoResponse response = getServerInfo(httpClient, url);
				if (response != null) {
					// Response Date
					String serverDateAsString = removeMicroSeconds(response.getResult().getInfo().getTime());
					Date date = DATE_FORMAT.parse(serverDateAsString);
					// Response Index
					Integer responseIndex = response.getResult().getInfo().getValidatedLedger().getSeq();

					// Add the record
					Record record = new Record();
					record.setTime(start == null ? 0 : ((date.getTime() - start) / 1000));
					record.setIncrement(firstIndex == null ? 0 : responseIndex - firstIndex);
					records.add(record);
					System.out.println("- Time (s): " + record.getTime());
					System.out.println("- Increment: " + record.getIncrement());

					if (start == null) {
						start = date.getTime();
					}
					if (firstIndex == null) {
						firstIndex = responseIndex;
					}
					lastIndex = responseIndex;
				}
				Thread.sleep(interval * 1000);
			}

			// Write data file
			String dataFile = writeDataFile(capturePath, records);

			// Write plot script file
			String plotScriptPath = writePlotScript(capturePath, id, dataFile);

			// Run GNU PLOT command
			Runtime.getRuntime().exec("gnuplot " + plotScriptPath);

			// Get statistics
			LedgerStats stats = getStats(httpClient, url, firstIndex, lastIndex);

			System.out.println("[INFO] Capture terminated");
			System.out.println("- Path: " + capturePath);
			System.out.println("- Min time (s): " + stats.getMinTime());
			System.out.println("- Max time (s): " + stats.getMaxTime());
			System.out.println("- Average time (s): " + stats.getAverageTime());

			//

			System.exit(0);

		} catch (Exception e) {
			System.out.println("[ERROR] Capture failed");
			System.out.println("- Error: " + e.getMessage());
			System.exit(1);
		}
	}

	private static String removeMicroSeconds(String dateAsString) {
		return dateAsString.substring(0, dateAsString.indexOf(".") + 4)
				+ dateAsString.substring(dateAsString.length() - 4);
	}

	private static String writeDataFile(String folderPath, List<Record> records) throws IOException {
		String fileName = folderPath + "/record.data";
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		for (Record record : records) {
			writer.write(record.getTime() + " ");
			writer.write(record.getIncrement() + "\n");
		}
		writer.close();
		return fileName;
	}

	private static LedgerStats getStats(OkHttpClient httpClient, String url, Integer firstIndex, Integer lastIndex) {
		List<Integer> times = new ArrayList<>();
		for (int i = firstIndex; i < lastIndex; i++) {
			System.out.println("[INFO] Requesting ledger details (Index=" + i + ") ...");
			LedgerResponse ledgerResponse = getLedger(httpClient, url, i);
			boolean closed = ledgerResponse.getResult().getLedger().isClosed();
			System.out.println("- Closed: " + closed);
			if (closed) {
				System.out.println("- Duration (s): " + ledgerResponse.getResult().getLedger().getClosingDuration());
				times.add(ledgerResponse.getResult().getLedger().getClosingDuration());
			}
		}
		LedgerStats stats = new LedgerStats();
		stats.setMaxTime(times.stream().mapToInt(v -> v).max().orElse(0));
		stats.setMinTime(times.stream().mapToInt(v -> v).min().orElse(0));
		stats.setAverageTime(times.stream().mapToInt(v -> v).average().orElse(0));
		return stats;
	}

	private static String writePlotScript(String folderPath, String title, String dataFile) throws IOException {
		String fileName = folderPath + "/script.p";
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		writer.write("set terminal png\n");
		writer.write("set xlabel 'Time (s)'\n");
		writer.write("set ylabel 'Increment'\n");
		writer.write("set output '" + folderPath + "/plot.png'\n");
		writer.write("set title '" + title + "'\n");
		writer.write("plot '" + dataFile + "' title '' with lines");
		writer.close();
		return fileName;
	}

	private static ServerInfoResponse getServerInfo(OkHttpClient httpClient, String url) {
		ServerInfoRequest serverInfoRequest = new ServerInfoRequest();
		serverInfoRequest.setMethod("server_info");

		Gson gson = new Gson();
		String json = gson.toJson(serverInfoRequest);
		@SuppressWarnings("deprecation")
		RequestBody body = RequestBody.create(JSON, json);

		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				return null;
			}
			return gson.fromJson(response.body().string(), ServerInfoResponse.class);
		} catch (IOException ioe) {
			return null;
		}
	}

	private static LedgerResponse getLedger(OkHttpClient httpClient, String url, Integer ledgerIndex) {
		LedgerRequest ledgerRequest = new LedgerRequest();
		ledgerRequest.setMethod("ledger");
		List<LedgerRequestParam> params = new ArrayList<>();
		LedgerRequestParam param = new LedgerRequestParam();
		param.setLedgerIndex(ledgerIndex);
		params.add(param);
		ledgerRequest.setParams(params);

		Gson gson = new Gson();
		String json = gson.toJson(ledgerRequest);
		@SuppressWarnings("deprecation")
		RequestBody body = RequestBody.create(JSON, json);

		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				return null;
			}
			return gson.fromJson(response.body().string(), LedgerResponse.class);
		} catch (IOException ioe) {
			return null;
		}
	}
}
