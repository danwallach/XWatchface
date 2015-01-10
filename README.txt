/**
 * XWatchface
 * Copyright (C) 2015 by Dan Wallach
 * Available under an Apache-style license (as with AOSP)
 */

This is a very simple Android Wear watchface based on the example watchface apps
distributed by Google.

What's useful and interesting is that it shows you how listen to broadcasts from a
stopwatch and timer and render them. Some useful things to examine:

AndroidMainfest.xml: This is pretty much exactly what you'd expect to see for any
watchface, except for the <receiver> element, which says that we're interested in
receiving broadcast intents from stopwatches and timers.

XWatchfaceService.java: This is very similar to the stock sweeping-hand watchface
code that you'll find in the examples that Google ships. What's new is the call to
XDrawTimers.draw() and XWatchFaceReceiver.pingExternalStopwatches().

XWatchfaceReceiver.java: When a stopwatch or timer has an update to send, this is
where we're going to receive it. You probably want to just include this entire file
without changing anything.

XDrawTimers.java: This is a very, very basic way to add stopwatch and timer rendering
into your own watchface. For the stopwatch, it draws a minute hand and a second hand.
If the stopwatch is reset, it draws nothing. If it's in ambient mode, it just draws
the minute hand. For the timer, it only draws one hand that sweeps counterclockwise,
starting and ending at 12 o'clock. You should, of course, feel free to embellish on
this and do something else.
