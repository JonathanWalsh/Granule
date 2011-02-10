import com.granule.CompressTagHandler
import com.granule.RealRequestProxy

class GranuleTagLib {
  def compress =  { attrs, body ->
     String method = attrs['method']
     String options = attrs['options']
     String basepath = attrs['basepath']
     String id = attrs['id']
     out << (new CompressTagHandler(id, method, options, basepath)).handleTag(new RealRequestProxy(request), body()).toString()
  }
}
