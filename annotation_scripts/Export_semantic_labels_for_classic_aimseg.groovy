def imageData = getCurrentImageData()

// Create output path (relative to project)
def outputDir = buildFilePath(PROJECT_BASE_DIR, 'export')
mkdirs(outputDir)

// Create output subfolders
def semanticDir = buildFilePath(outputDir, 'semantic')
mkdirs(semanticDir)
def instanceDir = buildFilePath(outputDir, 'instance')
mkdirs(instanceDir)
def imageDir = buildFilePath(outputDir, 'image')
mkdirs(imageDir)

// Define output file paths
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def pathSemantic = buildFilePath(semanticDir, name + ".tif")
def pathSemantic2 = buildFilePath(semanticDir, name + "_boundaries.tif")
def pathInstance = buildFilePath(instanceDir, name + ".tif")
def pathImage = buildFilePath(imageDir, name + ".tif")

// Define how much to downsample during export
double downsample = 4

// Create an ImageServer where the pixels are derived from annotations
def semanticServer1 = new LabeledImageServer.Builder(imageData)
  .backgroundLabel(0, ColorTools.BLACK) // Specify background label (usually 0 or 255)
  .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
  .addLabel('Outer', 1)      // Choose output labels (the order matters!)
  .addLabel('Inner', 2)
  .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
  .build()
  
def semanticServer2 = new LabeledImageServer.Builder(imageData)
  .backgroundLabel(0, ColorTools.BLACK) // Specify background label (usually 0 or 255)
  .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
  .addLabel('Axon', 2)
  .addLabel('AxonNM', 2)
  .lineThickness(2)
  .setBoundaryLabel('Boundary*', 3)
  .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
  .build()
  
def instanceServer = new LabeledImageServer.Builder(imageData)
  .backgroundLabel(0, ColorTools.BLACK) // Specify background label (usually 0 or 255)
  .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
  .useAnnotations()
  .useInstanceLabels()
  .useFilter(p -> p.isAnnotation() && p.getPathClass() == getPathClass('Outer'))
  .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
  .build()
  
// Create an ImageServer where raw images are downsampled
def server = imageData.getServer()
def region = RegionRequest.createInstance(server, downsample)

// Write the images
writeImage(semanticServer1, pathSemantic)
writeImage(semanticServer2, pathSemantic2)
writeImage(instanceServer, pathInstance)
writeImageRegion(server, region, pathImage)