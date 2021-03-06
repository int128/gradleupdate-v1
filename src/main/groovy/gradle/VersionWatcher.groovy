package gradle

import groovy.util.logging.Log
import groovyx.gaelyk.GaelykBindings
import infrastructure.GradleRegistry
import model.CurrentGradleVersion

@Log
@GaelykBindings
class VersionWatcher {

    private final registry = new GradleRegistry()

    def fetchStableVersion() {
        def cached = CurrentGradleVersion.get('stable')?.version
        if (cached) {
            cached
        } else {
            log.info("Fetching current stable release from Gradle registry")
            registry.fetchCurrentStableRelease().version
        }
    }

    def stableReleasesWithFixedIssues() {
        log.info("Fetching stable releases from Gradle registry")
        def versions = registry.fetchStableReleases()

        versions.take(1).each { version ->
            log.info("Fetching issues fixed in $version.version from Gradle registry")
            version.fixedIssues = registry.fetchIssuesFixedIn(version.version)
        }

        versions
    }

    def rcReleasesWithFixedIssues() {
        log.info("Fetching RC releases from Gradle registry")
        def versions = registry.fetchReleaseCandidateReleases()
        def rcFor = versions.find { it.rcFor }?.rcFor
        log.info("Fetching issues fixed in $rcFor from Gradle registry")
        def fixedIssues = registry.fetchIssuesFixedIn(rcFor)

        versions.each { version ->
            if (version.version == rcFor) {
                version.fixedIssues = fixedIssues
            } else if (version.rcFor == rcFor) {
                version.fixedIssues = fixedIssues.findAll { it.fixedin == version.version }
            }
        }

        versions
    }

    def performIfNewRcReleaseIsAvailable(Closure closure) {
        datastore.withTransaction {
            final last = CurrentGradleVersion.get('rc')?.version

            log.info("Fetching current RC release from Gradle registry")
            final current = registry.fetchCurrentReleaseCandidateRelease()?.version

            if (last == current) {
                log.info("RC version is still $current, do nothing")
            } else if (current) {
                log.info("Found the new RC $current, calling closure")
                closure(current)
                log.info("Saving the new RC $current into the datastore")
                new CurrentGradleVersion(label: 'rc', version: current).save()
            } else {
                log.info("Last RC $last is no longer available, removing from the datastore")
                CurrentGradleVersion.delete('rc')
            }
        }
    }

    def performIfNewStableReleaseIsAvailable(Closure closure) {
        datastore.withTransaction {
            final last = CurrentGradleVersion.get('stable')?.version

            log.info("Fetching current stable release from Gradle registry")
            final current = registry.fetchCurrentStableRelease().version

            if (last == current) {
                log.info("Stable version is still $current, do nothing")
            } else {
                log.info("Found the new stable $current, calling closure")
                closure(current)
                log.info("Saving the new stable $current into the datastore")
                new CurrentGradleVersion(label: 'stable', version: current).save()
            }
        }
    }

}
