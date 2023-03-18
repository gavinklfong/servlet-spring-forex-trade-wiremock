Feature: Forex Trade Service
	Verify Forex Trade Service Functionalities
	
	Scenario Outline: Submit a forex trade deal
		And I request for a rate booking with parameters: '<baseCurrency>', '<counterCurrency>', '<tradeAction>', <baseCurrencyAmount>, <customerId>
		And I should receive a valid rate booking
		When I submit a forex trade deal with rate booking and parameters: '<baseCurrency>', '<counterCurrency>', '<tradeAction>', <baseCurrencyAmount>, <customerId>
		Then I should get the forex trade deal successfully posted
		Examples:
		| baseCurrency| counterCurrency | tradeAction | baseCurrencyAmount| customerId |
		| NZD 				| CHF 		| BUY		    | 1000							| 1 				 |
		| NZD 				| CAD 		| BUY			| 250							| 1 				 |
		| CAD 				| JPY 		| SELL			| 2000							| 1 				 |
		| GBP 				| NZD 		| SELL			| 3000							| 1 				 |
		| EUR 				| CAD 		| BUY			| 1500							| 1 				 |
		| AUD 				| CHF 		| SELL			| 100000						| 1 				 |
		
	Scenario Outline: Retrieve trade deal by customer
		And I request for a rate booking with parameters: '<baseCurrency>', '<counterCurrency>', '<tradeAction>', <baseCurrencyAmount>, <customerId>
		And I should receive a valid rate booking
		When I submit a forex trade deal with rate booking and parameters: '<baseCurrency>', '<counterCurrency>', '<tradeAction>', <baseCurrencyAmount>, <customerId>
		And I should get the forex trade deal successfully posted
		When I request for forex trade deal by <customerId>
		Then I should get a list of forex trade deal for <customerId>
		Examples:
		| baseCurrency| counterCurrency | tradeAction | baseCurrencyAmount| customerId |
		| EUR 				| CAD 		| BUY			| 1500							| 1 				 |
		| AUD 				| CHF 		| SELL			| 100000						| 1 				 |
		
