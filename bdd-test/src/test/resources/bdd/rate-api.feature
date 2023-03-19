
Feature: Rate Service
	Verify Rate Service Functionalities
	
	Scenario Outline: Fetch the latest rate with base currency '<baseCurrency>'
		Given Forex rate available for base currency '<baseCurrency>'
		When Request for the latest rate with base currency '<baseCurrency>'
		Then Receive currency rates
		Examples:
			| baseCurrency |
			| GBP          |
			| USD          |
			| AUD          |
			| EUR          |
			| NZD          |

	Scenario Outline: Make a rate booking
		Given Forex rate available for base currency '<baseCurrency>' and counter currency '<counterCurrency>'
		And Existing customers:
			| id 			| name 		| tier 	|
			| <customerId>	| John Doe	| 10	|
		When Request for a rate booking with parameters: '<baseCurrency>', '<counterCurrency>', '<tradeAction>', <baseCurrencyAmount>, <customerId>
		Then Receive a valid rate booking
		Examples:
			| baseCurrency | counterCurrency | tradeAction | baseCurrencyAmount | customerId |
			| EUR          | USD             | BUY         | 1000               | 123        |
			| USD          | JPY             | BUY         | 250                | 12         |
			| GBP          | USD             | SELL        | 2000               | 999        |
			| AUD          | USD             | SELL        | 3000               | 999        |
			| NZD          | USD             | BUY         | 1500               | 100        |
			| EUR 		   | JPY 			 | SELL		   | 100000				| 100 		 |
		