package com.gatling.tests

import scala.concurrent.duration.*
import io.gatling.core.Predef.{feed, *}
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.jdbc.Predef.*
import sun.security.util.Length

import scala.collection.mutable
import scala.util.Random
import scala.annotation.tailrec
import scala.concurrent.duration.*
import io.gatling.jdbc.Predef.*


class registerUser extends Simulation {

	val httpProtocol: HttpProtocolBuilder = http
		.baseUrl("https://petstore.octoperf.com")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")

	def landingPage: ChainBuilder = {
		exec(http("CreateOrder_00_LandingPage")
			.get("/actions/Catalog.action"))
			.pause(2, 4)}

	def signin: ChainBuilder = {
		exec(http("CreateOrder_01_ClickSignIn")
			.get("/actions/Account.action?signonForm="))
			.pause(2, 4)}

	def register: ChainBuilder = {
		exec(http("RegisterUser_02_ClickRegisterNow")
			.get("/actions/Account.action?newAccountForm="))
			.pause(2, 4)}

	def saveAccount: ChainBuilder = {
			exec(http("RegisterUser_03_SaveAccountInfo")
				.post("/actions/Account.action")
	//			.formParam("username", "PerfTest0049")
				.formParam("password", "test")
				.formParam("repeatedPassword", "test")
				.formParam("account.firstName", randomString(10))
				.formParam("account.lastName", "Perftest")
				.formParam("account.email", randomString(10) + "@PerfTest.com")
				.formParam("account.phone", session => Random.nextInt(999999))
				.formParam("account.address1", "address1")
				.formParam("account.address2", "address2")
				.formParam("account.city", "QACity")
				.formParam("account.state", randomString(2))
				.formParam("account.zip", session => Random.nextInt(999999))
				.formParam("account.country", "QALand")
				.formParam("account.languagePreference", "english")
				.formParam("account.favouriteCategoryId", "FISH")
				.formParam("account.favouriteCategoryId", "FISH")
				.formParam("newAccount", "Save Account Information")
				.formParam("_sourcePage", "BLuumc2ZDJbpwFEvXhV_FLFsLKZybQT7ZUOCOMZj0zu3EPihy21IIpDTr3BElw_aJeICyodaAwzHGO-oGHRC_USBtKh6NlaaI1eEj8GILtY=")
				.formParam("__fp", "iHwf8lbiYK-rfGtHvdqdFWX-fpHppnUTXTivVf1pQbiSY0zsCgNvslsLQ66jmxW-dvXx-aonYl6iMv6rofmMkg4V3aJV4nROyoxF5T-BHgbBC0Y9G0X0R_cU1c8lMYHR1raQC9T1AfAf381Ugkfs0FDPQ8YTYCdDCWuVKsyrNuj-Pok_tIVh0MNzZFndsNxE"))
			.pause(2, 4)}

	def randomString(length: Int): String = {
		val r = new scala.util.Random
		val sb = new mutable.StringBuilder
		for (i <- 1 to length) {
			sb.append(r.nextPrintableChar)
		}
		sb.toString
	}

	val scn1: ScenarioBuilder = scenario("registerUser")
		.exec(landingPage,signin,register,saveAccount)

		setUp(scn1.inject(atOnceUsers(1))).protocols(httpProtocol)
}