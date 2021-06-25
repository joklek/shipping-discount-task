To run this program you will need at least Java 16.

First you'll have to run `build-shipping-discount-calculator` and then you can run `run-shipping-discount-calculator` as
many times as you want.

If you `run-shipping-discount-calculator` without arguments, it will take the file contents in `input.txt` in your main
directory. Otherwise, you can enter a second argument as the filepath to your input file.

## Layout of the code

All the magic starts in `Main` class, which creates the components it will be using, reads the file and passes lines of
text down the pipeline.

The first point I'd like to touch here is the fact that I'm passing individual lines of text
to `ShippingPriceCalculator`, not a list of them. This moves the responsibility of record keeping to the user of
the `ShippingPriceCalculator`. In theory, this would allow reading bigger files or easier adapting to some sort of event
queue.

First `ShippingInfoMapper` parses a raw line of text into the basic `ShippingInfo`. If the raw line of text is invalid
an exception will be thrown. Currently there are no custom exceptions implemented and `ShippingPriceCalculator` will
gobble up any exceptions thrown and just return an empty response.
(If the need of detailed exceptions would ever rise, `ShippingPriceCalculator` could return not `Optional` but
and `Either` which would hold either the success or error values)

If parsing was done successfully, `ShippingInfo` is passed down to `ShippingPriceProvider`, which is a fake "database",
which will return the default price of shipping for given carrier and package size.

Then `ShippingSuggestedPriceProvider` is used to find the suggested price by applying `DiscountRule`s, which take in the
ShippingInfo, initial price and current suggested price (As I see it now, I could also probably just pass the current
discount? All of the rules would work fine, but `ThirdLargeForLaPosteRule` would need an additional query to get the
Large LaPoste price). This was done to allow flexibility for the rules and avoid any weird coupling with upper levels of
the code.

Successful shipments are saved to `ShippingInfoRepo`, which is also used in some `DiscountRule`s, to allow the access of
historic data. I thought about passing a list of past decisions to the rules, but it sounded dirty. Also, imagining the
repo as a Database helped me feel safer with this design.

The most interesting part of this for me was how the Rules define the design. I made the rules as separate classes,
which are iterated through, each returning a suggested price. The order of this list is very important!

### SmallShipmentsRule

This is the simplest one, it uses `ShippingSuggestedPriceProvider` to get all the prices for given shipment size, picks
the lowest one and returns it. Why use the price provider in every step, instead on initialization? Yes, it would save
some sweet performance, but I did so while imagining the price provider as an external source of information. In that
case performance could be saved with having a cache inbetween.

### ThirdLargeForLaPosteRule

This rule uses shipment repo to get shipments on that month, for that carrier for that size, to check whether the
conditions apply.

### DiscountAccumulationLimitRule

This rule uses shipments repo to get shipments on that month and calculates the total amount left for discounts. Then it
decides whether the earlier steps assigned good discounts or should the discounts be reduced.

## Questions

* Is it guaranteed that the shipping lines will be provided ordered by date?
* Should the parser be strict about the number of whitespaces?
* If we add more rules, more requirements should be added to each rule. Should a discount be ignored, if an earlier
  discount is better? Not always?

