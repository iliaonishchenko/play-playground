package filters

import javax.inject.Inject

import play.api.http.HttpFilters
import play.filters.gzip.GzipFilter
import play.filters.headers.SecurityHeadersFilter

class Filters @Inject() (gz: GzipFilter, scoreFilter: ScoreFilter) extends HttpFilters {
	println("inside filters")
	val filters = Seq(scoreFilter, gz, SecurityHeadersFilter())
}
