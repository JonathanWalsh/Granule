import com.granule.CompressTagHandler
import com.granule.RealRequestProxy

class GranuleTagLib {
  def compress =  { attrs, body ->
     String method = attrs['method']
     String options = attrs['options']
     String basepath = attrs['basepath']
     String id = attrs['id']
     RealRequestProxy request = new RealRequestProxy(request)
     out << (new CompressTagHandler(id, method, options, basepath)).handleTag(request, request, body()).toString()
  }
}
