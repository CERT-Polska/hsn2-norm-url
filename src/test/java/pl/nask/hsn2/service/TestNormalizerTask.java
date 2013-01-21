/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.service;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import pl.nask.hsn2.ParameterException;
import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.TaskContext;

public class TestNormalizerTask {
	@Mocked
	private TaskContext job;
	
	@Test
	public void encodingTest() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("http://test.pl.:80/a%2b%2bc%?a%2bc=+b%ggg%");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://test.pl:80/a++c%25?a%2bc=%20b%25ggg%25");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("host", "test.pl");times=1;
				job.addAttribute("path", "/a++c%25");times=1;
				job.addAttribute("query","a%2bc=%20b%25ggg%25");times=1;
				job.addAttribute("sld", "test.pl");times=1;
				job.addAttribute("tld", "pl");times=1;
			}
		};
		t.process();
	}
	
	@Test
	public void testSimple() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("wp.pl");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://wp.pl/");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("host", "wp.pl");times=1;
				job.addAttribute("path", "/");times=1;
				job.addAttribute("sld", "wp.pl");times=1;
				job.addAttribute("tld", "pl");times=1;
			}
		};
		t.process();
	}
	@Test
	public void userInfoTest() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("uSer:@w%61p.pl");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "uSer:@wap.pl");times=1;
				job.addAttribute("protocol", "uSer");times=1;
				job.addAttribute("path", "@wap.pl");times=1;
				job.addAttribute("host", anyString);times=0;
			}
		};
		t.process();
	}
	@Test
	public void userInfoTest2() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("http://user:@w%61p.pl");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://user:@wap.pl/");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("path", "/");times=1;
				job.addAttribute("host", "wap.pl");times=1;
				job.addAttribute("tld","pl");times=1;
				job.addAttribute("sld", "wap.pl");times=1;
			}
		};
		t.process();
	}
	
	@Test
	public void testEmptyQuery() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("wp.pl/?#a=b");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://wp.pl/?#a=b");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("host", "wp.pl");times=1;
				job.addAttribute("path", "/");times=1;
				job.addAttribute("sld", "wp.pl");times=1;
				job.addAttribute("tld", "pl");times=1;
				job.addAttribute("query", "");times=1;
			}
		};
		t.process();
	}
	
	
	@Test //BUG#8841
	public void incorrectSchema() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("httpabC://wp.pl");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "httpabC://wp.pl");times=1;
				job.addAttribute("protocol", "httpabC");times=1;
				job.addAttribute("path", "//wp.pl");times=1;
			}
		};
		t.process();
	}
	@Test // BUG#8838
	public void pathSlashEncodingTest() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("http://www.nask.pl/wydarzeniaID%2fid/811?abc");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://www.nask.pl/wydarzeniaID%2fid/811?abc");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("host","www.nask.pl");times=1;
				job.addAttribute("tld", "pl");times=1;
				job.addAttribute("sld", "nask.pl");
				job.addAttribute("path", "/wydarzeniaID%2fid/811");times=1;
				job.addAttribute("query", "abc");times=1;
			}
		};
		t.process();
	}
	@Test // BUG#8800
	public void queryMarkTest() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("http://www.wp.pl/?field=value");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://www.wp.pl/?field=value");times=1;
				job.addAttribute("host", "www.wp.pl");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("path", "/");times=1;
				job.addAttribute("query", "field=value");times=1;
				job.addAttribute("tld", "pl");times=1;
				job.addAttribute("sld", "wp.pl");times=1;
			}
		};
		t.process();
	}
	@Test // BUG#8792
	public void noTLDTest() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("HTTPs://localhost");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "https://localhost/");times=1;
				job.addAttribute("host", "localhost");times=1;
				job.addAttribute("protocol", "https");times=1;
				job.addAttribute("path", "/");times=1;
				job.addAttribute("tld", "localhost");times=1;
				job.addAttribute("sld", anyString);times=0;
			}
		};
		t.process();
	
	}
	@Test
	public void emptyQueryTest() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("HTTPs://www.nask.pl.:80?");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "https://www.nask.pl:80/?");times=1;
				job.addAttribute("host", "www.nask.pl");times=1;
				job.addAttribute("protocol", "https");times=1;
				job.addAttribute("path", "/");times=1;
				job.addAttribute("tld", "pl");times=1;
				job.addAttribute("query", "");times=1;
			}
		};
		t.process();
	}
	
	@Test
	public void testSimple1() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("http://www.nask.pl");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://www.nask.pl/");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("host", "www.nask.pl");times=1;
				job.addAttribute("path", "/");times=1;
				job.addAttribute("sld", "nask.pl");times=1;
				job.addAttribute("tld", "pl");times=1;
				job.addAttribute("query", anyString);times=0;
			}
		};
		t.process();
	}
	@Test
	public void testNormalizeProperUrl() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("http://localhost:80/?aaa=bbb");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://localhost:80/?aaa=bbb");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("host", "localhost");times=1;
				job.addAttribute("path", "/");times=1;
				job.addAttribute("port", 80);times=1;
				job.addAttribute("query", "aaa=bbb"); times=1;
			}
		};
		t.process();
	}
	
	@Test
	public void obscureTest() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData("HTTP://!$^&*()_+`-={}|;@www.pc-help.org/obscur%65ł.htm?a=b;c=d");
		URLNormalizerTask t = new URLNormalizerTask(job, data);
		
		new NonStrictExpectations() {
			{
				job.addAttribute("url_normalized", "http://!$%5E&*()_+%60-=%7B%7D%7C%3B@www.pc-help.org/obscure%C5%82.htm?a=b&c=d");times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("host", "www.pc-help.org");times=1;
				job.addAttribute("path", "/obscure%C5%82.htm");times=1;
				job.addAttribute("query", "a=b&c=d"); times=1;
			}
		};
		t.process();
		
	
	}
	@Test(dataProvider="ip6v4")
	public void testIP6v4(String in,final String expected) throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData(in);
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{					
				job.addAttribute("url_normalized", expected);times=1;
				job.addAttribute("protocol", "http");times=1;
				job.addAttribute("host", anyString);times=1;
				job.addAttribute("path", anyString);times=1;
				job.addAttribute("sld", anyString); times=0;
				job.addAttribute("tld", anyString); times=0;
			}
		};
		t.process();
	}
	@DataProvider
	Object [][] ip6v4() {
		return new Object[][] {
				{"[0:0:0:0:0:ffff:212.77.100.101]","http://[0:0:0:0:0:ffff:212.77.100.101]/"},
				{"http://[0:0:0:0:0:ffff:212.77.100.101]/","http://[0:0:0:0:0:ffff:212.77.100.101]/"},
				{"http://[::212.77.100.101]","http://[0:0:0:0:0:0:212.77.100.101]/"},
				{"http://[::0:FFFF:212.77.100.101]","http://[0:0:0:0:0:ffff:212.77.100.101]/"}

		};
	}
	
	@Test
	public void testNormalizeWrongUrl() throws ParameterException, ResourceException, StorageException {
		ServiceData data = new ServiceData(":localhost");
		URLNormalizerTask t = new URLNormalizerTask(job, data );
		
		new NonStrictExpectations() {
			{					
				// nothing should be added				
				job.addAttribute(anyString, anyString);times=0;
			}
		};
		t.process();
	}
}
