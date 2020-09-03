package es.eriktorr

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._

package object image {
  type Quality = Double Refined Interval.Closed[0.1d, 1.0d]

  type MaxWidth8K = Int Refined Interval.Closed[1, 7680]
  type MaxHeight8K = Int Refined Interval.Closed[1, 4320]
}