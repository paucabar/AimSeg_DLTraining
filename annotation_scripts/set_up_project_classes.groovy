// Platform.runLater to call on the correct (UI) thread
Platform.runLater {
    // Be really careful here!
    // Need to include null for unclassified objects, and make sure all names are unique
    getQuPath().getAvailablePathClasses().setAll(
        getPathClass(null), // Important to keep this!
        getPathClass('AxonNMy', makeRGB(255, 0, 255)), 
        getPathClass('Fibre', makeRGB(0, 0, 255)),
        getPathClass('InRe', makeRGB(255, 0, 0)),
        getPathClass('AxonMy', makeRGB(255, 255, 0))
        getPathClass('Tile', makeRGB(255, 255, 255))
    )
}