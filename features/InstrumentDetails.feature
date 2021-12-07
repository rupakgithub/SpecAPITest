Feature: Test RDM APIs

@static
Scenario Outline: Verify InstrumentDetails Api
	When User made "<Type>" request to "<URL>" with "<RequestFile>" and compare "<ResponseCount>" and file "<ExpectedFile>"
	
Examples: {'datafile':'TestData/EMEA Pricing Run.json', filter = 'Execute == "Y"'} 

@dyna
Scenario Outline: Verify API structure
	When User made "<Type>" request to "<URL>" with "<RequestFile>"
	
Examples:  {'datafile':'TestData/ApiStructure.json', filter = 'Execute == "Y"'}	