Feature: Forex Trade Service
	Verify Forex Trade Service Functionalities
	
	Scenario Outline: Submit a forex trade deal
		Given Existing customers:
			| id 			| name 		| tier 	|
			| <customerId>	| John Doe	| 10	|
		*  Forex rate available for base currency '<baseCurrency>' and counter currency '<counterCurrency>'
		*  Request for a rate booking with parameters: '<baseCurrency>', '<counterCurrency>', '<tradeAction>', <baseCurrencyAmount>, <customerId>
		*  Receive a valid rate booking
		When Submit a forex trade deal with rate booking and parameters: '<baseCurrency>', '<counterCurrency>', '<tradeAction>', <baseCurrencyAmount>, <customerId>
		Then Receive a valid forex trade deal response
		Examples:
			| baseCurrency | counterCurrency | tradeAction | baseCurrencyAmount | customerId |
			| EUR          | USD             | BUY         | 1000               | 123        |
			| USD          | JPY             | BUY         | 250                | 12         |
			| GBP          | USD             | SELL        | 2000               | 999        |
			| AUD          | USD             | SELL        | 3000               | 999        |
			| NZD          | USD             | BUY         | 1500               | 100        |
			| EUR 		   | JPY 			 | SELL		   | 100000				| 100 		 |
		
	Scenario: Retrieve trade deal by customer
		And Existing customers:
			| id 	| name 		| tier 	|
			| 123	| John Doe	| 10	|
		Given Existing forex trade deals:
			| id | baseCurrency | counterCurrency | baseCurrencyAmount | customerId | dealRef  | rate | timestamp  		    | tradeAction |
			| 1  | GBP          | USD             | 100                | 123        | 4950c945 | 0.25 | 2023-01-15T15:00:23Z | BUY		 |
		When Request for forex trade deal by 123
		Then Receive a list of forex trade deal for 123
		
