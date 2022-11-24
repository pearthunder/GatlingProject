package com.gatling.tests

import scala.concurrent.duration.*
import scala.concurrent.duration.*
import io.gatling.core.Predef.*
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.jdbc.Predef.*

import scala.math.random
import scala.util.Random

class updateOrder extends Simulation {

	val httpProtocol: HttpProtocolBuilder = http
		.baseUrl("https://petstore.octoperf.com")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
		.upgradeInsecureRequestsHeader("1")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")

	val categoryfeeder: BatchableFeederBuilder[String]#F = csv("data/category.csv").random
	val usersfeeder: BatchableFeederBuilder[String] = csv("data/users.csv")



	def landingPage: ChainBuilder = {
		exec(http("CreateOrder_00_LandingPage")
			.get("/actions/Catalog.action"))
			.pause(2, 4)
	}

	def signin: ChainBuilder = {
		exec(http("CreateOrder_01_ClickSignIn")
			.get("/actions/Account.action?signonForm="))
			.pause(2, 4)
	}

	def login: ChainBuilder = {
		feed(usersfeeder)
			.exec(http("CreateOrder_02_Login")
				.post("/actions/Account.action")
				.formParam("username","PerfTest0044")
//				.formParam("username", session => session("username").as[String])
				.formParam("password", "test")
				.formParam("signon", "Login")
				.formParam("_sourcePage", "-PMbJqz9ufrn500p6jtGnNskDtrZB6I0cgoRw2fLu406xcQFYa8o9dWb20X44UFMVivwLTVnmvXOtqjlL9MkkQMnTadq063_CKheBGWDA0I=")
				.formParam("__fp", "sGbXHWiWTuWDen6vSd7K9mUorw6ptEWKR95kRfk4W9zVWqIcy_vEhc33DMcuC-A6"))
			.pause(2, 4)
	}

	def category: ChainBuilder = {
		feed(categoryfeeder)
			.exec(http("CreateOrder_03_SelectCategory ${categoryId}")
				.get("/actions/Catalog.action?viewCategory=&categoryId=" + "${categoryId}")
				.check(regex("""productId=(.*)"""").findRandom.saveAs("productId")))
			.pause(2, 4)
	}

	def product: ChainBuilder = {
		exec(http("CreateOrder_04_SelectProduct ${productId}")
			.get("/actions/Catalog.action?viewProduct=&productId=${productId}")
			.check(regex("""itemId=(.*)"""").findRandom.saveAs("itemId")))
			.pause(2, 4)
	}

	def viewItem: ChainBuilder = {
		exec(http("CreateOrder_05_ViewItem ${itemId}")
			.get("/actions/Catalog.action?viewItem=&itemId=${itemId}"))
			.pause(2, 4)
	}

	def cart: ChainBuilder = {
		exec(http("CreateOrder_05_AddToCart ${itemId}")
			.get("/actions/Cart.action?addItemToCart=&workingItemId=${itemId}"))
			.pause(2, 4)
	}

	def checkout: ChainBuilder = {

		exec(http("CreateOrder_06_ProceedCheckout")
			.get("/actions/Order.action?newOrderForm="))
			.pause(2, 4)
	}

	def continue: ChainBuilder = {
		exec(http("CreateOrder_07_PressContinue")
			.post("/actions/Order.action")
			.formParam("order.cardType", "Visa")
			.formParam("order.creditCard", "999 9999 9999 9999")
			.formParam("order.expiryDate", "12/03")
			.formParam("order.billToFirstName", "oeOOCY[2")
			.formParam("order.billToLastName", "Perftest")
			.formParam("order.billAddress1", "!&IBe,&ZPW")
			.formParam("order.billAddress2", "VDs3pG+E9K")
			.formParam("order.billCity", "Nw&3|")
			.formParam("order.billState", "Nw&3|")
			.formParam("order.billZip", "4")
			.formParam("order.billCountry", "QALand")
			.formParam("newOrder", "Continue")
			.formParam("_sourcePage", "ObTzFuQVGrEVdlLed37kwCcenzTkASuGEJrwU7mK27UXSvE8U7ukMXP4RuL1a3z3X_90lWCOiG4iFj3uIAODhsjwhnMYK62u3e6M2Xim-7E=")
			.formParam("__fp", "E0X6nRpfISKndAW7QLGAe9CdU-ofz-hbBnFrlyB2bes9Ufa6dxupfkdbPREvjVW0UuFP36qHnT5mKJHDgSpFkM5sb3e1vfCvTcqVufL9s1Okti-aBa2UJA=="))
			.pause(2, 4)
	}

	def confirm: ChainBuilder = {
		exec(http("CreateOrder_08_ConfirmOrder")
			.get("/actions/Order.action?newOrder=&confirmed=true"))
			.pause(2, 4)
	}

	private val times = 6 to 7

	val scn: ScenarioBuilder = scenario("createOrder")
		.exec(landingPage,signin,login)
		.exec(_.set("times", times))
		.repeat("${times.random()}" ) {
			exec(category,product,viewItem,cart)}
		.exec(checkout,continue,confirm)

/*
		.exec(http("request_132")
			.post("/actions/Cart.action")
			.formParam("EST-1", "2")
			.formParam("EST-28", "2")
			.formParam("EST-15", "2")
			.formParam("updateCartQuantities", "Update Cart")
			.formParam("_sourcePage", "IRRWxLmlYAoZURAPssKc-AlRSULlfrK6rg3BrW1iPg7vDXto_Pb4hQ6LiLsZl28dB2YzERIZ_FP_M_tUya3FjPDbYO0UhcdJ")
			.formParam("__fp", "GUef7xPoUYiIE-JIi9pbzZPyLTqBYMf6z90Az3j3AddaJJNRMhRy_ZMjL-ijWZf0"))
			.pause(2, 4)

		.exec(http("request_142")
			.get("/actions/Order.action?newOrderForm="))
		.pause(2, 4)

		.exec(http("request_152")
			.post("/actions/Order.action")
			.formParam("order.cardType", "Visa")
			.formParam("order.creditCard", "999 9999 9999 9999")
			.formParam("order.expiryDate", "12/03")
			.formParam("order.billToFirstName", "oeOOCY[2")
			.formParam("order.billToLastName", "Perftest")
			.formParam("order.billAddress1", "!&IBe,&ZPW")
			.formParam("order.billAddress2", "VDs3pG+E9K")
			.formParam("order.billCity", "Nw&3|")
			.formParam("order.billState", "Nw&3|")
			.formParam("order.billZip", "4")
			.formParam("order.billCountry", "QALand")
			.formParam("newOrder", "Continue")
			.formParam("_sourcePage", "OWE5fUFDou2ENn7oaA0p-i4RgxbBcpYfrhJ6cLwCaEx77xc10rKL6ad_j0-3RlFfpaFoJ9BTqC6TMHkw43EI63ryc_Fm8O0l5_X974Eytuk=")
			.formParam("__fp", "PHBwLkqHdKHgTu15pC5HE5bIyvK9S3JJWxr0-EzTseuFyg10D0P25egjEWBoRm3agQhSR1dYvtQEVBtjAjKlz7-_ou97t5ofp2EQ89qEnT9r6zapXTd_5Q=="))
		.pause(2, 4)

		.exec(http("request_162")
			.get("/actions/Order.action?newOrder=&confirmed=true"))
*/

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}