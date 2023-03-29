def imageData = getCurrentImageData()

// Create output path (relative to project)
def outputDir = buildFilePath(PROJECT_BASE_DIR, 'export')
mkdirs(outputDir)

// Create output subfolders
def instanceDir = buildFilePath(outputDir, 'instance')
mkdirs(instanceDir)
def imageDir = buildFilePath(outputDir, 'image')
mkdirs(imageDir)

// Define output file paths
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def pathInstance = buildFilePath(instanceDir, name + ".tif")
def pathImage = buildFilePath(imageDir, name + ".tif")

// Define how much to downsample during export
double downsample = 4
  
def instanceServer = new LabeledImageServer.Builder(imageData)
  .backgroundLabel(0, ColorTools.BLACK) // Specify background label (usually 0 or 255)
  .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
  .useAnnotations()
  .useInstanceLabels()
  .useFilter(p -> p.getPathClass() == getPathClass('Axon') || p.getPathClass() == getPathClass('AxonNM'))
  .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
  .build()
  
// Create an ImageServer where raw images are downsampled
def server = imageData.getServer()
def region = RegionRequest.createInstance(server, downsample)

// Write the images
writeImage(instanceServer, pathInstance)
writeImageRegion(server, region, pathImage)