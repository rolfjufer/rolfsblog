#!/usr/bin/env bash
#
# Creates a directory for a new blog post from _template/ and adds it
# to the index table in README.md.
#
# Usage: ./new-post.sh <directory> <artifact-id> "<title>" <url>
# Example:
#   ./new-post.sh 2027-01-spring-modulith-events spring-modulith-events \
#       "Events in Spring Modulith" https://rolfsblog.ch/events-in-spring-modulith/
#
set -euo pipefail

if [[ $# -ne 4 ]]; then
    echo "Usage: $0 <directory> <artifact-id> \"<title>\" <url>" >&2
    exit 1
fi

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export POST_DIR="$1"
export ARTIFACT_ID="$2"
export TITLE="$3"
export URL="$4"

if [[ ! "${POST_DIR}" =~ ^[0-9]{4}-[0-9]{2}-[a-z0-9-]+$ ]]; then
    echo "Error: directory must look like JJJJ-MM-kurzname, e.g. 2027-01-spring-modulith-events" >&2
    exit 1
fi
if [[ ! "${ARTIFACT_ID}" =~ ^[a-z0-9-]+$ ]]; then
    echo "Error: artifact-id may only contain lowercase letters, digits and hyphens" >&2
    exit 1
fi
if [[ "${TITLE}" == *"&"* || "${TITLE}" == *"<"* ]]; then
    echo "Error: the title must not contain & or < (it is written into pom.xml)" >&2
    exit 1
fi
if [[ -e "${ROOT}/${POST_DIR}" ]]; then
    echo "Error: ${POST_DIR} already exists" >&2
    exit 1
fi

cp -R "${ROOT}/_template" "${ROOT}/${POST_DIR}"
for file in "${ROOT}/${POST_DIR}/README.md" "${ROOT}/${POST_DIR}/pom.xml"; do
    perl -pi -e 's/\{\{TITLE\}\}/$ENV{TITLE}/g; s/\{\{URL\}\}/$ENV{URL}/g; s/\{\{ARTIFACT_ID\}\}/$ENV{ARTIFACT_ID}/g' "${file}"
done

export ROW="| [\`${POST_DIR}\`](${POST_DIR}/) | [${TITLE}](${URL}) | Java 25, Maven, JUnit |"
perl -0pi -e 's/(<!-- POSTS:END -->)/$ENV{ROW}\n$1/' "${ROOT}/README.md"

echo "Created ${POST_DIR}/ and added it to README.md."
echo "Next: add your code under ${POST_DIR}/src and fill in ${POST_DIR}/README.md."
