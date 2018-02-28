# ApiResponses
RestApi made easy in android

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  Step 2. Add the dependency
  
  dependencies {
	        compile 'com.github.vagishd:ApiResponses:1.0'
	}
  
  
  Sample Code -:
  
 Implement ResponseListner interface in acivity/fragment.
  
 ApiResponse apiResponse = ApiResponse.with(this, this);
 apiResponse.performJsonParsing(true/false) // perform json parsing or not.
 apiResponse.setClassType(ClassName.class) // Response class
 apiResponse.METHOD'S
 
 public void getRequestParams(String TAG, String url, HashMap<String, String> params)
 
 public void getRequest(String TAG, String Url)
 
 public void postRequest(String TAG, String url)
 
 public void postRequestParams(String TAG, String url, HashMap<String, String> params)
  
 public void postFile(String TAG, String url, HashMap<String, File> fileHashMap, HashMap<String, String> params)
  
  
