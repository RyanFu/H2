package com.prettygirl.app.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.http.AndroidHttpClient;
import android.util.Log;

/**
 * HTTP Utilities.
 * 
 * <p>
 * Here is a example:
 * 
 * <pre>
 *     Http.get("http://www.baidu.com", new Handler() {
 *         \@Override
 *         public void handleMessage(Message message) {
 *             Bundle data = message.getData();
 *             int status = data.getInt(Http.RESPONSE_STATUS);
 *             String body = data.getString(Http.RESPONSE_BODY);
 *             Log.d(LOGTAG, "Status: " + status + ", Body: " + body);
 *         }
 *     });
 * </pre>
 * 
 * </p>
 * 
 */
public class Http {

	private static final String USER_AGENT = "beauty.newlib";

	private static final Header HEADER_GZIP = new BasicHeader(
			"Accept-Encoding", "gzip");

	private static final String TAG = "Http";

	public static final int STATUS_OK = 200;

    public static final int HTTP_CONNECTION_TIME_OUT = 2000;
    
	public static final String DEFAULT_ENCODING = "UTF-8";

	private static final boolean LOGE_ENABLED = false;

	private static final boolean LOGD_ENABLED = false;

	private final Context mContext;

	public Http(Context context) {
		this.mContext = context;
	}

	/**
	 * Send a HTTP GET request to specified <code>url</code>, then call
	 * <code>handler</code> on response parsed.
	 * <p>
	 * The handler will only be invoked after response completely received and
	 * parsed, if any error causes (eg. {@link IOException}) the handler will
	 * NOT be invoked.
	 * </p>
	 * <p>
	 * Specially, a response with error status code (not 200) is legal, it means
	 * the handler will be called as expected in this case.
	 * </p>
	 * 
	 * @param url
	 *            The target URL request will send to
	 * @param callback
	 *            Handler will be fired on response parsed
	 */
	public void get(final String url, final Callback<String> callback) {
		// Prepare a response handler
		final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response)
					throws IOException {

				StatusLine statusLine = response.getStatusLine();
				if (statusLine == null) {
					return null;
				}
				int status = statusLine.getStatusCode();
				String content = retrieveContent(response);
				callback.onSuccess(url, status, content);
				return content;
			}

		};

		// New a thread to perform request asynchronously
		new Thread() {
			@Override
			public void run() {
				AndroidHttpClient client = null;
				try {
					client = AndroidHttpClient.newInstance(USER_AGENT);
					HttpClientParams.setRedirecting(client.getParams(), true);
					HttpConnectionParams.setSoTimeout(client.getParams(), HTTP_CONNECTION_TIME_OUT);
					// Build request
					HttpUriRequest request = new HttpGet(url);
					setProxyIfNecessary(request);
					request.addHeader(HEADER_GZIP);

					// Execute request
					client.execute(request, responseHandler);
				} catch (ClientProtocolException e) {
					Log.e(TAG, "HTTP protocol error.", e);
					callback.onFailure(url, e);
				} catch (IOException e) {
					Log.e(TAG, "Perform GET request failed.", e);
					callback.onFailure(url, e);
				} catch (RuntimeException e) {
					Log.e(TAG, "Unexpected exception has thrown.", e);
					callback.onFailure(url, e);
				} finally {
					if (client != null) {
						client.close();
					}
				}
			}
		}.start();
	}

	public String get(final String url) {
		AndroidHttpClient client = null;
		try {
			client = AndroidHttpClient.newInstance(USER_AGENT);
			HttpClientParams.setRedirecting(client.getParams(), true);
            HttpConnectionParams.setSoTimeout(client.getParams(), HTTP_CONNECTION_TIME_OUT);
			// Build request
			HttpUriRequest request = new HttpGet(url);
			setProxyIfNecessary(request);
			request.addHeader(HEADER_GZIP);

			// Execute request
			HttpResponse response = client.execute(request);

			StatusLine statusLine = response.getStatusLine();
			if (statusLine == null) {
				return null;
			}
			int status = statusLine.getStatusCode();
			if (status != Http.STATUS_OK) {
				return null;
			}
			String content = retrieveContent(response);
			return content;
		} catch (ClientProtocolException e) {
			Log.e(TAG, "HTTP protocol error.", e);
		} catch (IOException e) {
			Log.e(TAG, "Perform GET request failed.", e);
		} catch (RuntimeException e) {
			Log.e(TAG, "Unexpected exception has thrown.", e);
		} finally {
			if (client != null) {
				client.close();
			}
		}

		return null;
	}

	public String post(String url, List<NameValuePair> params,
			boolean retrieveContent) {
		AndroidHttpClient client = null;
		try {
			client = AndroidHttpClient.newInstance(USER_AGENT);
			HttpClientParams.setRedirecting(client.getParams(), true);
			// Build request
			HttpPost request = new HttpPost(url);
			if (params != null) {
				request.setEntity(new UrlEncodedFormEntity(params,
						DEFAULT_ENCODING));
			}
			setProxyIfNecessary(request);
			// Execute request
			HttpResponse response = client.execute(request);

			StatusLine statusLine = response.getStatusLine();
			if (statusLine == null) {
				return null;
			}
			int status = statusLine.getStatusCode();
			if (status != Http.STATUS_OK) {
				if (LOGE_ENABLED) {
					Log.e(TAG, "Status Code is invalid: " + status);
				}
				return null;
			}

			if (retrieveContent) {
				String content = retrieveContent(response);

				if (LOGD_ENABLED) {
					Log.d(TAG, "status: " + status + ", content: " + content);
				}

				return content;
			} else {
				return "";
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, "HTTP protocol error.", e);
		} catch (IOException e) {
			Log.e(TAG, "Perform GET request failed.", e);
		} catch (RuntimeException e) {
			Log.e(TAG, "Unexpected exception has thrown.", e);
		} finally {
			if (client != null) {
				client.close();
			}
		}

		return null;
	}

	public boolean get(final String url, final File target) {
		AndroidHttpClient client = null;
		try {
			client = AndroidHttpClient.newInstance(USER_AGENT);
			HttpClientParams.setRedirecting(client.getParams(), true);
			// Build request
			HttpUriRequest request = new HttpGet(url);
			setProxyIfNecessary(request);
			request.addHeader(HEADER_GZIP);

			// Execute request
			HttpResponse response = client.execute(request);

			StatusLine statusLine = response.getStatusLine();
			if (statusLine == null) {
				return false;
			}
			int status = statusLine.getStatusCode();
			if (status != STATUS_OK) {
				return false;
			}
			try {
				writeToFile(response, target);
			} catch (IOException e) {
				// 没写入成功却建了一个空文件，这个时候要删除它
				if (target != null && target.exists()) {
					target.delete();
				}
				return false;
			}
			if (LOGD_ENABLED) {
				Log.d(TAG, "status: " + status + ", target: " + target);
			}
			return true;
		} catch (ClientProtocolException e) {
			Log.e(TAG, "HTTP protocol error.", e);
		} catch (IOException e) {
			Log.e(TAG, "Perform GET request failed.", e);
		} catch (RuntimeException e) {
			Log.e(TAG, "Unexpected exception has thrown.", e);
		} finally {
			if (client != null) {
				client.close();
			}
		}

		return false;
	}

	/**
	 * Get URL content and save it to file.
	 * 
	 * @param url
	 *            URL to retrieve
	 * @param target
	 *            Target file to store content
	 * @param callback
	 *            Fire on all content saved
	 */
	public void get(final String url, final File target,
			final Callback<File> callback) {
		// Prepare a response handler
		final ResponseHandler<File> responseHandler = new ResponseHandler<File>() {
			@Override
			public File handleResponse(HttpResponse response)
					throws IOException {
				StatusLine statusLine = response.getStatusLine();
				if (statusLine == null) {
					return null;
				}
				int status = statusLine.getStatusCode();
				if (status != STATUS_OK) {
					return null;
				}
				try {
					writeToFile(response, target);
				} catch (IOException e) {
					// 没写入成功却建了一个空文件，这个时候要删除它
					if (target != null && target.exists()) {
						target.delete();
					}
					callback.onFailure(url, e);
					return target;
				}
				if (LOGD_ENABLED) {
					Log.d(TAG, "status: " + status + ", target: " + target);
				}
				callback.onSuccess(url, status, target);
				return target;
			}
		};

		// New a thread to perform request asynchronously
		new Thread() {
			@Override
			public void run() {
				AndroidHttpClient client = null;
				try {
					client = AndroidHttpClient.newInstance(USER_AGENT);
					HttpClientParams.setRedirecting(client.getParams(), true);
					// Build request
					HttpUriRequest request = new HttpGet(url);
					setProxyIfNecessary(request);
					request.addHeader(HEADER_GZIP);
					// Execute request
					client.execute(request, responseHandler);
				} catch (ClientProtocolException e) {
					Log.e(TAG, "HTTP protocol error.", e);
					callback.onFailure(url, e);
				} catch (IOException e) {
					Log.e(TAG, "Perform GET request failed.", e);
					callback.onFailure(url, e);
				} catch (RuntimeException e) {
					Log.e(TAG, "Unexpected exception has thrown.", e);
					callback.onFailure(url, e);
				} finally {
					if (client != null) {
						client.close();
					}
				}
			}
		}.start();
	}

	/**
	 * Set proxy on the request if necessary.
	 * 
	 * @param request
	 *            Request setting proxy
	 */
	@SuppressWarnings("deprecation")
	private void setProxyIfNecessary(HttpUriRequest request) {
		ConnectivityManager mConnectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = mConnectivity == null ? null : mConnectivity
				.getActiveNetworkInfo();
		if (networkInfo == null
				|| networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return;
		}
		String proxyHost = Proxy.getHost(mContext);
		if (proxyHost == null) {
			return;
		}
		int proxyPort = Proxy.getPort(mContext);
		if (proxyPort < 0) {
			return;
		}
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		ConnRouteParams.setDefaultProxy(request.getParams(), proxy);
	}

	/**
	 * Write HTTP response to file.
	 * 
	 * @param response
	 *            HTTP response
	 * @param target
	 *            Target file
	 * @throws IOException
	 *             If any IO exception
	 */
	private static void writeToFile(HttpResponse response, File target)
			throws IOException {
		InputStream input = null;
		try {
			input = getEntityContent(response);
			File parent = target.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			FileUtils.copyInputStreamToFile(input, target);
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	/**
	 * Retrieve content from response body.
	 * <p>
	 * This method will uncompress content if there is 'Content-Encoding: gzip'
	 * in the header of response, otherwise just return raw content.
	 * </p>
	 * 
	 * @param response
	 *            HTTP response object
	 * @return Content of response body
	 * @throws IOException
	 *             If any exception about IO
	 */
	public static String retrieveContent(HttpResponse response)
			throws IOException {
		InputStream input = getEntityContent(response);
		return inputStreamToString(input);
	}

	/**
	 * Get entity content.
	 * <p>
	 * If the response content encoding is gzip, the method will unzip
	 * automatically.
	 * </p>
	 * 
	 * @param response
	 *            HTTP response
	 * @return Entity content input stream
	 * @throws IOException
	 *             If any IO exception
	 */
	private static InputStream getEntityContent(HttpResponse response)
			throws IOException {
		HttpEntity entity = response.getEntity();
		InputStream input = containsValue(entity.getContentEncoding(), "gzip") ? AndroidHttpClient
				.getUngzippedContent(entity) : entity.getContent();
		return input;
	}

	/**
	 * Convert a {@link InputStream} to {@link String}.
	 * 
	 * @param inputStream
	 *            Converting input stream object
	 * @return Converted string
	 * @throws IOException
	 *             If any IO exception
	 */
	private static String inputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (content.length() != 0) {
					content.append((char) Character.LINE_SEPARATOR);
				}
				content.append(line);
			}
			return content.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

	}

	/**
	 * Check whether the specified <code>header</code> contains a
	 * <code>value</code>.
	 * 
	 * @param header
	 *            HTTP request/response header
	 * @param value
	 *            Checking value
	 * @return <code>true</code> if the <code>header</code> contains the
	 *         <code>value</code>, <code>false</code> otherwise
	 */
	private static boolean containsValue(Header header, String value) {
		if (header == null || value == null) {
			return false;
		}
		for (HeaderElement element : header.getElements()) {
			if (value.equals(element.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Callback for the HTTP client.
	 * 
	 * @author GuoLin
	 */
	public static interface Callback<T> {

		/**
		 * Fire on response complete, no matter what status is.
		 * 
		 * @param status
		 *            Response status code
		 * @param result
		 *            Result from response
		 */
		public void onSuccess(String url, int status, T result);

		/**
		 * Fire on request failed, may connection error.
		 * 
		 * @param e
		 *            Error
		 */
		public void onFailure(String url, Exception e);
	}

	public Bitmap get(String url, boolean lowQualityFlag) {
		AndroidHttpClient client = null;
		try {
			client = AndroidHttpClient.newInstance(USER_AGENT);
			HttpClientParams.setRedirecting(client.getParams(), true);
			// Build request
			HttpUriRequest request = new HttpGet(url);
			setProxyIfNecessary(request);
			request.addHeader(HEADER_GZIP);

			// Execute request
			HttpResponse response = client.execute(request);

			StatusLine statusLine = response.getStatusLine();
			if (statusLine == null) {
				return null;
			}
			int status = statusLine.getStatusCode();
			if (status != STATUS_OK) {
				return null;
			}
			InputStream input = getEntityContent(response);
			Bitmap bitmap = null;
			try {
				bitmap = BitmapUtils.decodeStream(input, lowQualityFlag);
			} catch (OutOfMemoryError e) {
				bitmap = null;
			}
			return bitmap;
		} catch (ClientProtocolException e) {
			Log.e(TAG, "HTTP protocol error.", e);
		} catch (IOException e) {
			Log.e(TAG, "Perform GET request failed.", e);
		} catch (RuntimeException e) {
			Log.e(TAG, "Unexpected exception has thrown.", e);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

}
